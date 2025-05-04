// Firebase config
const firebaseConfig = {
  apiKey: "AIzaSyCpIAp_3toinNiwbQ7xeDKF8ieToEGVYsI",
  authDomain: "catchu-datingapp.firebaseapp.com",
  projectId: "catchu-datingapp",
  storageBucket: "catchu-datingapp.firebasestorage.app",
  messagingSenderId: "367797428924",
  appId: "1:367797428924:web:8b894d02f9c7aa085b66b9"
};

// Initialize Firebase
firebase.initializeApp(firebaseConfig);
const db = firebase.firestore(); 