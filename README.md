# Auto Message - Sales & Logistics Automation Platform

## Overview
Auto Message is a professional Android application designed to automate communication for sales professionals, delivery personnel, and supply chain operators. Built with Kotlin and Jetpack Compose, it acts as a reliable communication assistant that automatically replies to received calls with context-specific, customizable message templates, ensuring that no business lead or logistical update goes unaddressed when you are occupied.

## Business Problem
In fast-paced sales and supply chain environments, professionals are often driving, in meetings, or handling critical tasks, making them unable to actively converse on incoming calls. Receiving a call from a client, vendor, or dispatcher when you cannot speak can lead to miscommunication or perceived unprofessionalism. The challenge is maintaining constant, professional communication and relationship management without compromising safety or current task focus.

## Solution
Auto Message solves these challenges by providing a fully automated, offline-capable auto-reply system. By categorizing contacts into specific groups (e.g., VIP Clients, Dispatchers, Vendors) and setting up customized message templates, the platform ensures that every received call triggers an immediate, relevant response via SMS. This maintains high professional standards, keeps stakeholders informed, and allows sales and logistics teams to focus on their primary tasks uninterrupted.

## Key Features
- **Automated Communication System**: Automatically respond to received calls with predefined, professional messages.
- **Client & Vendor Management**: Organize contacts with priority settings and blacklist features to tailor responses.
- **Message Templates**: Create and manage custom message templates for different scenarios (e.g., "In a client meeting", "Driving delivery route").
- **Group Organization**: Categorize contacts into business-relevant groups (e.g., Sales Leads, Logistics Team, VIP Clients).
- **Communication Logs**: Track all automated replies and received calls with a detailed history log.
- **Offline Functionality**: Operates completely offline with local data storage, crucial for drivers in low-network areas.
- **Modern Glassmorphism UI**: Intuitive and beautiful user interface with full dark mode support.

## System Architecture
The application follows a modern Android architectural pattern based on MVVM (Model-View-ViewModel) to ensure separation of concerns and maintainability.
- **UI Layer**: Built entirely with Jetpack Compose, utilizing StateFlow for reactive UI updates based on the current data state.
- **Domain Layer**: Handles the business logic for call detection, contact management, and message dispatching.
- **Data Layer**: Powered by Room database for robust local storage of logs, templates, and contact preferences.
- **Dependency Injection**: Managed via Hilt to provide scalable and testable component lifecycles.
- **Background Processing**: Utilizes Kotlin Coroutines and background services to monitor phone state and trigger SMS actions reliably.

## Technology Stack

Frontend:
- Kotlin
- Jetpack Compose
- Material Design 3

Backend:
- N/A (Complete Offline Architecture for privacy and reliability)

Database:
- Room Database (SQLite)

AI/ML:
- *(No AI/ML features currently implemented in this project)*

Cloud/Deployment:
- Local Android Deployment (No cloud dependency)

## Screenshots

### Dashboard
![Dashboard](docs/screenshots/dashboard.png)

### Sales Module
*(Maps to Contacts & Groups Management)*
![Sales Module](docs/screenshots/sales.png)

### Inventory Module
*(Maps to Message Templates Management)*
![Inventory](docs/screenshots/inventory.png)

### Analytics
*(Maps to Call History & Logs)*
![Analytics](docs/screenshots/analytics.png)

## Demo Video
[Watch Demo Video](https://drive.google.com/file/d/10JKgQ0Il-legKnyOIr0kxSIhcdXldkxz/view?usp=sharing)

## Installation
1. **Prerequisites**: Ensure you have Android Studio Arctic Fox (or later) and JDK 11+ installed.
2. **Clone Repository**:
   ```bash
   git clone https://github.com/Avi6855/AutoMessage.git
   cd AutoMessage
   ```
3. **Open Project**: Launch Android Studio and open the cloned `AutoMessage` directory.
4. **Sync**: Allow Android Studio to sync Gradle dependencies automatically.

## Configuration
The application requires specific Android permissions to function as an automation tool:
- `READ_CONTACTS`: To identify clients and vendors.
- `READ_CALL_LOG` & `READ_PHONE_STATE`: To detect received calls.
- `SEND_SMS` & `RECEIVE_SMS`: To dispatch automated replies.
- `POST_NOTIFICATIONS`: For status updates (Android 13+).

**In-App Configuration**:
- **Reply Delay**: Configurable delay before sending an automated message (e.g., 10 seconds).
- **Default Message**: Set a fallback response for uncategorized contacts.

## Running the Application
1. Connect a physical Android device or start an Android Emulator (API 21+).
2. To build and run directly via command line:
   ```bash
   ./gradlew installDebug
   ```
   Or click **Run 'app'** (`Shift+F10`) in Android Studio.

## API Documentation
*This application is fully offline and does not expose external web APIs. Internal database interactions are managed via Room DAOs.*

## Project Structure
```text
AutoMessage/
├── app/
│   ├── src/main/java/     # Kotlin source code (MVVM Architecture)
│   ├── src/main/res/      # Android resources (drawables, values)
│   └── build.gradle.kts   # App-level build configurations
├── gradle/                # Gradle wrapper files
├── build.gradle.kts       # Project-level build configurations
└── README.md              # Project documentation
```

## Security Considerations
- **Data Privacy**: All contact data, message templates, and communication logs are stored purely locally on the device.
- **No Analytics**: Zero third-party tracking or analytics ensures business contacts remain confidential.
- **Minimal Permissions**: The app gracefully requests and handles only the permissions strictly necessary for its communication automation.

## Scalability
- **Database Optimization**: Room database queries are optimized with appropriate indexing to handle thousands of call logs and contact records without UI freezing.
- **Reactive Architecture**: StateFlow ensures that the UI scales fluidly with state changes, avoiding memory leaks during long-term background execution.

## Future Enhancements
- Integration with CRM systems (e.g., Salesforce, HubSpot) to log interactions automatically.
- AI-powered message generation based on the caller's identity and time of day.
- WhatsApp Business API integration for multi-channel automated replies.
- Advanced analytics dashboard for tracking automated response success rates.

## Author

Name:
Avinash Patil

GitHub:
https://github.com/Avi6855

LinkedIn:
https://www.linkedin.com/in/avinash-patil-278011228/

## License

MIT License
