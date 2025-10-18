# Droid Manager - Know Your Droid

Android device system vitals monitoring application that provides real-time insights into device performance, collects historical data, and offers real-time graphical analytics about your Android device's operation.

![Droid Manager](https://github.com/user-attachments/assets/000babb7-3772-41ff-b26c-909877209dc1)

## 📷 Screenshots
![7](https://github.com/user-attachments/assets/02248ae1-3640-4fbb-b365-b144aadd07ed)
![8](https://github.com/user-attachments/assets/f763acb6-7835-4c5b-ad6a-9c15b55fc7a3)
![9](https://github.com/user-attachments/assets/45641d04-cfc8-4466-aaf2-39c3fdaa1b6c)
  

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

