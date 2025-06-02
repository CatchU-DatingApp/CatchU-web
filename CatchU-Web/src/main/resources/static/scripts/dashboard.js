const chartColors = {
    purple: 'rgba(119, 85, 204, 1)',
    lightPurple: 'rgba(119, 85, 204, 0.2)',
    pink: 'rgba(232, 71, 134, 1)',
    lightPink: 'rgba(232, 71, 134, 0.2)',
    blue: 'rgba(66, 194, 255, 1)',
    lightBlue: 'rgba(66, 194, 255, 0.2)',
    green: 'rgba(40, 167, 69, 1)',
    lightGreen: 'rgba(40, 167, 69, 0.2)',
    yellow: 'rgba(255, 193, 7, 1)',
    lightYellow: 'rgba(255, 193, 7, 0.2)',
    colorArray: [
        'rgba(119, 85, 204, 0.8)',
        'rgba(232, 71, 134, 0.8)',
        'rgba(66, 194, 255, 0.8)',
        'rgba(40, 167, 69, 0.8)',
        'rgba(255, 193, 7, 0.8)',
        'rgba(108, 117, 125, 0.8)',
        'rgba(220, 53, 69, 0.8)',
        'rgba(23, 162, 184, 0.8)',
        'rgba(255, 127, 80, 0.8)',
        'rgba(128, 0, 128, 0.8)'
    ]
};

async function loadStats() {
    try {
        const response = await fetch('/users');
        const users = await response.json();
        const today = new Date();
        let activeToday = 0;
        const dailyUsers = {};
        const ages = [];
        const genders = {};
        const interests = {};

        for (let i = 29; i >= 0; i--) {
            const date = new Date();
            date.setDate(date.getDate() - i);
            const dateStr = date.toISOString().split('T')[0];
            dailyUsers[dateStr] = 0;
        }

        users.forEach(user => {
            if (user.umur && !isNaN(user.umur)) {
                ages.push(user.umur);
            }

            if (user.gender) {
                genders[user.gender] = (genders[user.gender] || 0) + 1;
            }

            if (Array.isArray(user.interest)) {
                user.interest.forEach(i => {
                    interests[i] = (interests[i] || 0) + 1;
                });
            }

            const login = user.lastLogin ? convertFirestoreTimestamp(user.lastLogin) : null;
            if (login && login.toDateString() === today.toDateString()) {
                activeToday++;
            }

            const creationDate = login || new Date();
            const dateStr = creationDate.toISOString().split('T')[0];
            if (dailyUsers.hasOwnProperty(dateStr)) {
                dailyUsers[dateStr]++;
            }
        });

        const avgAge = ages.length ? Math.round(ages.reduce((sum, age) => sum + age, 0) / ages.length) : 0;
        let maleCount = genders['Male'] || 0;
        let femaleCount = genders['Female'] || 0;
        let totalGenderCount = maleCount + femaleCount;
        let ratioText = totalGenderCount ? `${Math.round((maleCount / totalGenderCount) * 100)}/${Math.round((femaleCount / totalGenderCount) * 100)}` : "0/0";

        document.getElementById("total-users").textContent = users.length;
        document.getElementById("active-today").textContent = activeToday;
        document.getElementById("avg-age").textContent = avgAge;
        document.getElementById("gender-ratio").textContent = ratioText;

        drawCharts(dailyUsers, ages, genders, interests);
    } catch (error) {
        console.error("Error loading data:", error);
    }
}
function convertFirestoreTimestamp(ts) {
    if (!ts || typeof ts.seconds !== 'number') return null;
    return new Date(ts.seconds * 1000 + Math.floor(ts.nanos / 1e6));
}

function drawCharts(dailyUsers, ages, genders, interests) {
    Chart.defaults.font.family = "'Segoe UI', sans-serif";
    Chart.defaults.color = '#6c757d';

    const ctx1 = document.getElementById('dailyUsersChart').getContext('2d');
    new Chart(ctx1, {
        type: 'line',
        data: {
            labels: Object.keys(dailyUsers),
            datasets: [{
                label: 'New Users',
                data: Object.values(dailyUsers),
                borderColor: chartColors.purple,
                backgroundColor: chartColors.lightPurple,
                tension: 0.3,
                fill: true,
                pointBackgroundColor: chartColors.purple,
                pointRadius: 3,
                pointHoverRadius: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false }, tooltip: { backgroundColor: 'rgba(0,0,0,0.7)', padding: 10, cornerRadius: 4 } },
            scales: { x: { grid: { display: false }, ticks: { maxTicksLimit: 7 } }, y: { beginAtZero: true, grid: { borderDash: [5, 5] }, ticks: { precision: 0 } } }
        }
    });

    const ctx2 = document.getElementById('ageDistributionChart').getContext('2d');
    new Chart(ctx2, {
        type: 'bar',
        data: {
            labels: ['<18', '18-24', '25-34', '35-44', '45+'],
            datasets: [{
                label: 'Users',
                data: [
                    ages.filter(a => a < 18).length,
                    ages.filter(a => a >= 18 && a <= 24).length,
                    ages.filter(a => a >= 25 && a <= 34).length,
                    ages.filter(a => a >= 35 && a <= 44).length,
                    ages.filter(a => a >= 45).length
                ],
                backgroundColor: chartColors.pink,
                borderRadius: 6
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: { x: { grid: { display: false } }, y: { beginAtZero: true, grid: { borderDash: [5, 5] }, ticks: { precision: 0 } } }
        }
    });

    const ctx3 = document.getElementById('genderChart').getContext('2d');
    new Chart(ctx3, {
        type: 'doughnut',
        data: {
            labels: Object.keys(genders),
            datasets: [{
                data: Object.values(genders),
                backgroundColor: [chartColors.blue, chartColors.pink, chartColors.yellow],
                borderWidth: 0,
                hoverOffset: 5
            }]
        },
        options: {
            responsive: true,
            maintainAspectRatio: false,
            cutout: '65%',
            plugins: { legend: { position: 'bottom', labels: { padding: 20, usePointStyle: true, pointStyle: 'circle' } } }
        }
    });

    const sortedInterests = Object.entries(interests).sort((a, b) => b[1] - a[1]).slice(0, 8);
    const interestLabels = sortedInterests.map(item => item[0]);
    const interestData = sortedInterests.map(item => item[1]);

    const ctx4 = document.getElementById('interestChart').getContext('2d');
    new Chart(ctx4, {
        type: 'bar',
        data: {
            labels: interestLabels,
            datasets: [{ axis: 'y', label: 'Users', data: interestData, backgroundColor: chartColors.colorArray, borderRadius: 6 }]
        },
        options: {
            indexAxis: 'y',
            responsive: true,
            maintainAspectRatio: false,
            plugins: { legend: { display: false } },
            scales: {
                x: { beginAtZero: true, grid: { borderDash: [5, 5] }, ticks: { precision: 0 } },
                y: { grid: { display: false } }
            }
        }
    });
}

loadStats();
setInterval(loadStats, 300000);