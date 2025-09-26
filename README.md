# Droid Manager - Know Your Droid

Android device system vitals monitoring application that provides real-time insights into device performance, collects historical data, and offers real-time graphical analytics about your Android device's operation.


## 📷 Screenshots

<table>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/f2770558-9d1e-4e73-acfe-62547f3b2f3c" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/2d0410cb-f4f2-43f6-bd1a-03fb74bf4782" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/32773f1a-ee10-4fab-990b-c018797d2348" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/bc3f23cf-253c-415e-b259-9baecd0a999d" width="200"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/0f9a4947-1aaa-4fc0-81d1-e453cc5c6137" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/1b3b8e80-c333-412b-beb7-2b6e39f55d9d" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/fc5590fe-91dc-4ad3-892b-fbb39a54c8a2" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/f1284e08-154d-48e2-9fc0-9b9396de9bb7" width="200"></td>
  </tr>
  <tr>
    <td><img src="https://github.com/user-attachments/assets/5189a18d-4129-46bf-8d2c-78bafb91c83a" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/cf95c46d-c9e8-4499-9bda-4eac72419a91" width="200"></td>
    <td><img src="https://github.com/user-attachments/assets/c55f007c-679c-4e08-b34c-0bbe7c9fc6a8" width="200"></td>
  </tr>

</table>

## 📥 Download
<p align="">
  <a href="https://github.com/shubhampandey45/DroidManager/releases/download/v0.1.0-alpha/app-debug.apk">
    <img src="https://img.shields.io/badge/Download-Droid Manager-green?style=for-the-badge&logo=windows" />
  </a>
</p>

## ✨ Features

### 📶 Dashboard 
- **CPU Performance, Memory Usage, Battery Status, Internal Storage, Network Activity**

### 📊 Live Stats
- **Live Grphical Representation of CPU Performance, Memory Usage, Battery Level along with System Health bar**

### 📱 Sessions
- **Database Info Card, Session wise data details**

### ⏱️ History
- **Database Info, Delete and Clear Hostory, Stats History with timeline**

### 🔧 Background Monitoring using Foreground Service
- **Continuous data collection via interactive foreground service notification**: 

### Requirements
- **Minimum SDK**: Android 7.0 (API 24)
- **Target SDK**: Android 15 (API 35)
- **Permissions**: Network access, location (for WiFi stats), notifications

## 🏗️ Architecture

Droid Manager follows **Clean Architecture** principles with clear separation of concerns:

```
┌─────────────────┐    ┌──────────────────┐    ┌─────────────────┐
│  Presentation   │    │     Domain       │    │      Data       │
│                 │    │                  │    │                 │
│ • UI (Compose)  │◄──►│ • Repository     │◄──►│ • Database      │
│ • ViewModels    │    │   Interface      │    │ • Data Sources  │
│ • Navigation    │    │ • Models         │    │ • Services      │
└─────────────────┘    └──────────────────┘    └─────────────────┘
```

### 📂 Project Structure

```
com.sp45.androidmanager/
├── 📁 data/
│   ├── collector/      # System data collection
│   ├── database/       # Room database components  
│   ├── repository/     # Repository implementation
│   └── service/        # Background monitoring service
├── 📁 domain/
│   ├── model/          # Domain models
│   └── repository/     # Repository interface
├── 📁 presentation/
│   ├── ui/            # Compose UI components
│   ├── navigation/    # Navigation setup
│   └── viewmodel/     # State management
└── 📁 di/             # Dependency injection modules
```

## 🛠️ Libraries & Components

### Core Android
- **Jetpack Compose, Room Database, ViewModel, Coroutines, Flow, Hilt & Foreground Service**

### Data Collection
- **Linux /proc filesystem** - CPU, memory, and process information
- **Android System Services** - Battery, activity, and storage managers
- **TrafficStats** - Network usage statistics

### Live Stats
- **Custom Sparklines** - Real-time data visualization

### Other Components
- **GSON** - JSON serialization for complex data types

## 🚀 Future Improvements

### 🔮 Planned Features
- [ ] **Alerts & Notifications** - Reminder/Threshold-based alerts
- [ ] **Widget Support** - Home screen monitoring widgets
- [ ] **Export Functionality** - CSV/JSON data export

### 🌟 UI/UX Improvements
- [ ] **Interactive Charts** 
- [ ] **Themes & Personalization**

