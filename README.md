# 🎯 LuckyHub — YouTube Giveaway Picker

![Spring Boot](https://img.shields.io/badge/Backend-SpringBoot-green)
![React](https://img.shields.io/badge/Frontend-React-blue)
---
[![Live Demo](https://img.shields.io/badge/Demo-Live%20Link-brightgreen?style=for-the-badge&logo=vercel)](https://lucky-hub.vercel.app/)

---

## ❗ Problem Statement

Running giveaways manually becomes a serious problem when:

* Comments scales
* Manual selection becomes:

  * ❌ Biased
  * ❌ Time-consuming
  * ❌ Non-scalable

Also:

* Duplicate users spam comments
* Hard to ensure fairness

---

## 💡 Solution

LuckyHub solves this using an **automated backend system**:

* Fetches comments from multiple videos
* Filters duplicate users
* Applies keyword filters (optional)
* Uses **unbiased selection logic**
* Supports **multiple winners at scale**

---

## 🏗️ System Architecture


```text
React (Frontend)
        ↓
Spring Boot (REST APIs)
        ↓
Redis (Caching / Rate Limiting)
        ↓
MySQL (Database)
        ↓
YouTube Data API
```

---

## ✨ Key Engineering Features

### ⚡ Performance Optimization (Redis)

* Used for **rate limiting** (YouTube API quota handling)
* Reduces repeated API calls using caching
* Helps handle **traffic spikes / cold starts**

---

### 🔐 Security (Stateless Authentication)

* **Spring Security** for backend protection
* **OAuth2 (Google Login)** for verified users
* **JWT (Stateless Auth)** for session handling
* Secure token validation for APIs

---

## 🧠 Core Business Logic

1. Fetch comments from videos
2. Merge all comment data
3. Remove duplicate users
4. Apply keyword filters
5. Count frequency per user
6. Select winners:

   * Whose comment contains keyword
   * Most frequent Who is commenting on regular basis OR
   * Who commented first 

---
## ⚙️ Setup Guide

### 1. Clone Repository

```bash
git clone https://github.com/prabhatsingh415/LuckyHub.git
cd LuckyHub
```

---

### 2. Backend Setup

```bash
cd Backend
mvn clean install
mvn spring-boot:run
```

---

### 3. Frontend Setup

```bash
cd Frontend
npm install
npm run dev
```

---

## 🔑 Environment Variables

```env
JWT_SECRET=${YOUR_SECRET}
DB_URL=${YOUR_DB_URL}
DB_USERNAME=${YOUR_DB_USER}
DB_PASSWORD=${YOUR_DB_PASS}
GOOGLE_CLIENT_ID=${YOUR_CLIENT_ID}
YOUTUBE_API_KEY=${YOUR_API_KEY}
```

## 💎 Subscription Logic

| Plan    | Limits                |
| ------- | --------------------- |
| FREE    | Limited winners/month |
| GOLD    | Higher limits         |
| DIAMOND | Maximum usage         |

* Checked before winner selection
* Stored in DB
* Enforced via backend

---

## 👨‍💻 Author

**Prabhat Singh**

* GitHub: https://github.com/prabhatsingh415
* LinkedIn: https://www.linkedin.com/in/prabhat-singh-rj415/
* Portfolio: https://prabhatsingh-two.vercel.app/
* X(twitter): https://x.com/Prabhatsingh415

---

## ⭐ Support

If you like this project:

👉 Give it a **star ⭐**
👉 Share it
👉 Use it in your giveaways

---
