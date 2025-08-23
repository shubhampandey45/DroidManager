# Droid Manager - Know Your Droid

Android device system vitals monitoring application that provides real-time insights into device performance, collects historical data, and offers real-time graphical analytics about your Android device's operation.


## 📷 Demo & Screenshots
Coming soon

## 📥 Download
Coming soon

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

### UI & Visualization
- **Material Design 3, Compose Navigation**
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

