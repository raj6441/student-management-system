# Digital Wallet Application

A comprehensive digital wallet application built with **Core Java**, **JSP**, and **MySQL**. This application provides secure user authentication, wallet management, and transaction processing capabilities.

## Features

### 🔐 User Management
- User registration with validation
- Secure login/logout functionality
- User profile management

### 💰 Wallet Operations
- View current wallet balance
- Deposit money to wallet
- Withdraw money from wallet
- Transfer money between users

### 📊 Transaction Management
- Complete transaction history
- Real-time transaction processing
- Transaction categorization (Credit, Debit, Transfer In/Out)
- Unique reference numbers for all transactions

### 🎨 Modern UI/UX
- Responsive design for all devices
- Modern gradient-based styling
- Intuitive navigation
- Real-time feedback and validation

## Technology Stack

- **Backend**: Core Java, JSP, Servlets
- **Frontend**: HTML5, CSS3, JavaScript
- **Database**: MySQL
- **Server**: Apache Tomcat (recommended)
- **Architecture**: MVC Pattern with DAO Layer

## Project Structure

```
wallet-app/
├── database/
│   └── wallet_schema.sql          # Database schema
├── src/main/
│   ├── java/com/wallet/
│   │   ├── model/                 # Entity classes
│   │   │   ├── User.java
│   │   │   ├── Wallet.java
│   │   │   └── Transaction.java
│   │   ├── dao/                   # Data Access Objects
│   │   │   ├── UserDAO.java
│   │   │   ├── WalletDAO.java
│   │   │   └── TransactionDAO.java
│   │   ├── servlet/               # Servlet Controllers
│   │   │   ├── LoginServlet.java
│   │   │   ├── RegisterServlet.java
│   │   │   ├── DashboardServlet.java
│   │   │   ├── TransactionServlet.java
│   │   │   └── LogoutServlet.java
│   │   └── util/                  # Utility classes
│   │       └── DatabaseConnection.java
│   └── webapp/
│       ├── WEB-INF/
│       │   └── web.xml            # Deployment descriptor
│       ├── css/
│       │   └── style.css          # Application styles
│       ├── login.jsp              # Login page
│       ├── register.jsp           # Registration page
│       ├── dashboard.jsp          # Main dashboard
│       ├── transaction.jsp        # Transaction operations
│       ├── error.jsp              # Error page
│       └── index.jsp              # Landing page
```

## Prerequisites

- **Java Development Kit (JDK) 8+**
- **Apache Tomcat 9.0+**
- **MySQL 8.0+**
- **MySQL Connector/J (JDBC Driver)**

## Installation & Setup

### 1. Database Setup

1. Install MySQL and create a database:
```sql
CREATE DATABASE wallet_db;
```

2. Run the database schema:
```bash
mysql -u root -p wallet_db < database/wallet_schema.sql
```

3. Update database credentials in `DatabaseConnection.java`:
```java
private static final String DB_URL = "jdbc:mysql://localhost:3306/wallet_db";
private static final String DB_USERNAME = "your_username";
private static final String DB_PASSWORD = "your_password";
```

### 2. Application Setup

1. **Download MySQL Connector/J**:
   - Download from [MySQL Official Site](https://dev.mysql.com/downloads/connector/j/)
   - Place `mysql-connector-java-x.x.x.jar` in `src/main/webapp/WEB-INF/lib/`

2. **Compile the Application**:
```bash
# Navigate to project root
cd wallet-app

# Compile Java files (ensure CLASSPATH includes servlet-api.jar and mysql-connector.jar)
javac -cp "path/to/servlet-api.jar:path/to/mysql-connector.jar" -d src/main/webapp/WEB-INF/classes src/main/java/com/wallet/*/*.java
```

3. **Deploy to Tomcat**:
   - Copy the entire `wallet-app` folder to Tomcat's `webapps` directory
   - Or create a WAR file and deploy

4. **Start Tomcat**:
```bash
# Start Tomcat server
./catalina.sh start  # Linux/Mac
catalina.bat start   # Windows
```

5. **Access Application**:
   - Open browser and navigate to: `http://localhost:8080/wallet-app`

## Default Users

The application comes with sample users for testing:

| Username | Password | Initial Balance |
|----------|----------|-----------------|
| admin    | admin123 | $1,000.00      |
| john_doe | password123 | $500.00     |

## Usage Guide

### 1. User Registration
- Navigate to the registration page
- Fill in required details (username, email, full name, password)
- Phone number is optional
- Upon successful registration, a wallet with $0 balance is created

### 2. Login
- Use your username and password to log in
- Session is maintained for 30 minutes of inactivity

### 3. Dashboard
- View current wallet balance
- See recent transactions
- Quick access to deposit, withdraw, and transfer operations

### 4. Transactions

#### Deposit Money
- Click "Deposit" from dashboard or navigation
- Enter amount and optional description
- Confirm transaction

#### Withdraw Money
- Click "Withdraw" from dashboard or navigation
- Enter amount (must not exceed current balance)
- Add optional description
- Confirm withdrawal

#### Transfer Money
- Click "Transfer" from dashboard or navigation
- Enter recipient's username
- Specify amount and description
- Confirm transfer (atomic operation with rollback support)

## Security Features

- **SQL Injection Prevention**: Prepared statements used throughout
- **Session Management**: Secure session handling with timeout
- **Input Validation**: Client-side and server-side validation
- **Transaction Integrity**: Atomic operations with rollback support
- **Error Handling**: Comprehensive error handling and user feedback

## Database Schema

### Users Table
- `user_id` (Primary Key)
- `username` (Unique)
- `email` (Unique)
- `password`
- `full_name`
- `phone`
- `created_at`, `updated_at`

### Wallets Table
- `wallet_id` (Primary Key)
- `user_id` (Foreign Key)
- `balance`
- `currency`
- `created_at`, `updated_at`

### Transactions Table
- `transaction_id` (Primary Key)
- `wallet_id` (Foreign Key)
- `transaction_type` (CREDIT, DEBIT, TRANSFER_IN, TRANSFER_OUT)
- `amount`
- `description`
- `reference_number` (Unique)
- `status` (PENDING, COMPLETED, FAILED)
- `created_at`

## API Endpoints

| Endpoint | Method | Description |
|----------|--------|-------------|
| `/login` | GET/POST | User authentication |
| `/register` | GET/POST | User registration |
| `/dashboard` | GET | Main dashboard |
| `/transaction` | GET/POST | Transaction operations |
| `/logout` | GET/POST | User logout |

## Troubleshooting

### Common Issues

1. **Database Connection Error**:
   - Verify MySQL is running
   - Check database credentials in `DatabaseConnection.java`
   - Ensure MySQL Connector/J is in classpath

2. **Compilation Errors**:
   - Verify JDK version compatibility
   - Ensure servlet-api.jar is in classpath
   - Check import statements

3. **Deployment Issues**:
   - Verify Tomcat version compatibility
   - Check web.xml configuration
   - Ensure all required JARs are in WEB-INF/lib

4. **CSS/Styling Issues**:
   - Clear browser cache
   - Verify CSS file path in JSP pages
   - Check MIME type configuration in web.xml

## Future Enhancements

- **Multi-currency Support**: Support for different currencies
- **Transaction Limits**: Daily/monthly transaction limits
- **Email Notifications**: Email alerts for transactions
- **Mobile App**: React Native or Flutter mobile application
- **REST API**: RESTful API for third-party integrations
- **Advanced Security**: Two-factor authentication, encryption
- **Analytics Dashboard**: Transaction analytics and reporting
- **Payment Gateway Integration**: Integration with external payment providers

## Contributing

1. Fork the repository
2. Create a feature branch
3. Commit your changes
4. Push to the branch
5. Create a Pull Request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

For support and questions:
- Create an issue in the repository
- Email: support@walletapp.com

---

**Built with ❤️ using Core Java, JSP, and MySQL**