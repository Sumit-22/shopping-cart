# BlinkShop - E-Commerce App

BlinkShop is a fully functional **e-commerce application** built using **Kotlin**, following the **MVVM architecture**. It provides a seamless shopping experience with features like authentication, product management, cart functionality, and payment integration.

## 📌 Features
- **User & Admin Modules**: Separate interfaces for customers and administrators.
- **Authentication**: Secure login and registration with Firebase Authentication.
- **Product Management**: CRUD operations for products (Add, Edit, Delete, View).
- **Cart & Checkout**: Users can add/remove items and proceed to checkout.
- **Payment Integration**: Razorpay integration for secure payments.
- **Search & Filtering**: Enhanced user experience with product search and category filters.
- **Order Tracking**: Users can track their orders in real-time.
- **Dark Mode Support**: UI adapts to system theme settings.

## 🏗️ Architecture & Design Pattern
BlinkShop follows the **MVVM (Model-View-ViewModel) architecture**, ensuring a clean separation of concerns and better maintainability.

- **Model**: Defines data structures and business logic.
- **View**: UI layer (XML + Jetpack Compose for certain components).
- **ViewModel**: Handles UI logic, LiveData, and communicates with repositories.
- **Repository**: Manages data sources (Firebase, API calls, local storage).

## 🛠️ Tech Stack
- **Language**: Kotlin
- **UI Framework**: XML 
- **Architecture**: MVVM
- **Database**: Firebase Firestore
- **Networking**: Retrofit + Coroutines
- **State Management**: LiveData & ViewModel
- **Payment Integration**: Razorpay
- **Local Storage**: Shared Preferences/Room Database
- **Version Control**: Git & GitHub

## 🚀 Installation & Setup
### Prerequisites
- Android Studio (latest version)
- Firebase Project with Firestore setup
- Razorpay API Key

### Steps
1. **Clone the Repository**
   ```sh
   git clone https://github.com/Sumit-22/shopping-cart.git
   ```
2. **Open in Android Studio**
3. **Configure Firebase**
   - Add `google-services.json` to the `app/` directory.
4. **Run the App**
   - Build and install the app on an emulator or physical device.

## 📸 Screenshots
(Add screenshots showcasing different features of the app)

## 🛠️ Future Enhancements
- Implement GraphQL for better API performance.
- Add Wishlist functionality.
- Introduce AI-powered product recommendations.

## 🤝 Contributing
Contributions are welcome! Feel free to fork the repository and submit a PR.

## 📄 License
This project is licensed under the MIT License - see the [LICENSE](LICENSE) file for details.

---
🔥 Developed with ❤️ by [Sumit Kumar](https://github.com/Sumit-22)
