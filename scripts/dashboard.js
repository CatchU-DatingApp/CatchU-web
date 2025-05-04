// Chart colors
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
    const snapshot = await db.collection("Users").get();
    const today = new Date();
    let activeToday = 0;
    const dailyUsers = {};
    const ages = [];
    const genders = {};
    const interests = {};
    
    // Initialize last 30 days data
    for (let i = 29; i >= 0; i--) {
      const date = new Date();
      date.setDate(date.getDate() - i);
      const dateStr = date.toISOString().split('T')[0];
      dailyUsers[dateStr] = 0;
    }

    snapshot.forEach(doc => {
      const user = doc.data();

      // Ages
      if (user.umur && !isNaN(user.umur)) {
        ages.push(user.umur);
      }

      // Gender
      if (user.gender) {
        genders[user.gender] = (genders[user.gender] || 0) + 1;
      }

      // Interests
      if (Array.isArray(user.interest)) {
        user.interest.forEach(i => {
          interests[i] = (interests[i] || 0) + 1;
        });
      }

      // Active today
      const login = user.lastLogin?.seconds ? new Date(user.lastLogin.seconds * 1000) : null;
      if (login && login.toDateString() === today.toDateString()) {
        activeToday++;
      }

      // Daily users
      const creationDate = user.createdAt?.seconds ? 
        new Date(user.createdAt.seconds * 1000) : 
        (login || new Date());
      const dateStr = creationDate.toISOString().split('T')[0];
      
      // Only count if within our 30 day window
      if (dailyUsers.hasOwnProperty(dateStr)) {
        dailyUsers[dateStr] = (dailyUsers[dateStr] || 0) + 1;
      }
    });

    // Calculate average age
    const avgAge = ages.length ? Math.round(ages.reduce((sum, age) => sum + age, 0) / ages.length) : 0;
    
    // Calculate gender ratio
    let maleCount = genders['Male'] || 0;
    let femaleCount = genders['Female'] || 0;
    let totalGenderCount = maleCount + femaleCount;
    let ratioText = totalGenderCount ? `${Math.round((maleCount / totalGenderCount) * 100)}/${Math.round((femaleCount / totalGenderCount) * 100)}` : "0/0";

    // Show stats
    document.getElementById("total-users").textContent = snapshot.size;
    document.getElementById("active-today").textContent = activeToday;
    document.getElementById("avg-age").textContent = avgAge;
    document.getElementById("gender-ratio").textContent = ratioText;

    drawCharts(dailyUsers, ages, genders, interests);
  } catch (error) {
    console.error("Error loading data:", error);
    // Show fallback UI or error message
  }
}

function drawCharts(dailyUsers, ages, genders, interests) {
  // Set Chart.js defaults
  Chart.defaults.font.family = "'Segoe UI', sans-serif";
  Chart.defaults.color = '#6c757d';
  
  // 1. Daily Users Chart
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
      plugins: {
        legend: {
          display: false
        },
        tooltip: {
          backgroundColor: 'rgba(0,0,0,0.7)',
          padding: 10,
          cornerRadius: 4
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          },
          ticks: {
            maxTicksLimit: 7
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            borderDash: [5, 5]
          },
          ticks: {
            precision: 0
          }
        }
      }
    }
  });

  // 2. Age Distribution Chart
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
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          grid: {
            display: false
          }
        },
        y: {
          beginAtZero: true,
          grid: {
            borderDash: [5, 5]
          },
          ticks: {
            precision: 0
          }
        }
      }
    }
  });

  // 3. Gender Chart
  const ctx3 = document.getElementById('genderChart').getContext('2d');
  new Chart(ctx3, {
    type: 'doughnut',
    data: {
      labels: Object.keys(genders),
      datasets: [{
        data: Object.values(genders),
        backgroundColor: [
          chartColors.blue,
          chartColors.pink,
          chartColors.yellow
        ],
        borderWidth: 0,
        hoverOffset: 5
      }]
    },
    options: {
      responsive: true,
      maintainAspectRatio: false,
      cutout: '65%',
      plugins: {
        legend: {
          position: 'bottom',
          labels: {
            padding: 20,
            usePointStyle: true,
            pointStyle: 'circle'
          }
        }
      }
    }
  });

  // 4. Interests Chart (Horizontal Bar)
  // Sort interests and take top 8
  const sortedInterests = Object.entries(interests)
    .sort((a, b) => b[1] - a[1])
    .slice(0, 8);
  
  const interestLabels = sortedInterests.map(item => item[0]);
  const interestData = sortedInterests.map(item => item[1]);
  
  const ctx4 = document.getElementById('interestChart').getContext('2d');
  new Chart(ctx4, {
    type: 'bar',
    data: {
      labels: interestLabels,
      datasets: [{
        axis: 'y',
        label: 'Users',
        data: interestData,
        backgroundColor: chartColors.colorArray,
        borderRadius: 6
      }]
    },
    options: {
      indexAxis: 'y',
      responsive: true,
      maintainAspectRatio: false,
      plugins: {
        legend: {
          display: false
        }
      },
      scales: {
        x: {
          beginAtZero: true,
          grid: {
            borderDash: [5, 5]
          },
          ticks: {
            precision: 0
          }
        },
        y: {
          grid: {
            display: false
          }
        }
      }
    }
  });
}

// Initialize
loadStats();

// Update stats every 5 minutes
setInterval(loadStats, 300000);

// Sidebar Users click event
window.addEventListener('DOMContentLoaded', function() {
  const sidebarItems = document.querySelectorAll('.sidebar .nav-item');
  const usersSidebar = Array.from(sidebarItems).find(item => item.textContent.trim().includes('Users'));
  const mainContent = document.querySelector('.main-content');
  const userListContainer = document.getElementById('user-list-container');

  if (usersSidebar) {
    usersSidebar.addEventListener('click', async function() {
      // Highlight sidebar
      sidebarItems.forEach(item => item.classList.remove('active'));
      usersSidebar.classList.add('active');
      // Hide dashboard content, show user list
      mainContent.querySelectorAll(':scope > *:not(#user-list-container)').forEach(el => el.style.display = 'none');
      userListContainer.style.display = 'block';
      userListContainer.innerHTML = '<h2 style="margin-bottom:16px;">Daftar User</h2><div id="user-list-loading">Loading...</div>';
      try {
        const snapshot = await db.collection('Users').get();
        let html = '<ul style="list-style:none;padding:0;">';
        snapshot.forEach(doc => {
          const user = doc.data();
          html += `<li style='padding:8px 0;border-bottom:1px solid #eee;'>${user.nama || user.name || '(Tanpa Nama)'}</li>`;
        });
        html += '</ul>';
        userListContainer.innerHTML = '<h2 style="margin-bottom:16px;">Daftar User</h2>' + html;
      } catch (e) {
        userListContainer.innerHTML = '<span style="color:red">Gagal memuat data user.</span>';
      }
    });
  }

  // Sidebar Dashboard click: tampilkan dashboard, sembunyikan user list
  const dashboardSidebar = Array.from(sidebarItems).find(item => item.textContent.trim().includes('Dashboard'));
  if (dashboardSidebar) {
    dashboardSidebar.addEventListener('click', function() {
      sidebarItems.forEach(item => item.classList.remove('active'));
      dashboardSidebar.classList.add('active');
      mainContent.querySelectorAll(':scope > *:not(#user-list-container)').forEach(el => el.style.display = '');
      userListContainer.style.display = 'none';
    });
  }
}); 