# Vanguard Monorepo

A modern full-stack monorepo featuring an Angular TypeScript frontend and a Java Spring Boot backend.

## 🏗️ Architecture

```
Vangaurd/
├── apps/
│   ├── frontend/          # Angular TypeScript application
│   └── backend/           # Spring Boot Java application
├── package.json           # Root package.json with scripts
├── README.md             # This file
└── .gitignore            # Git ignore patterns
```

## 🚀 Quick Start

### Prerequisites

- **Node.js** (v18 or higher)
- **Java** (JDK 17 or higher)
- **npm** or **yarn**

### Installation

1. Clone the repository:
```bash
git clone <repository-url>
cd Vangaurd
```

2. Install dependencies:
```bash
npm run setup
```

### Development

Start both frontend and backend in development mode:
```bash
npm run dev
```

This will start:
- **Frontend**: http://localhost:4200 (Angular dev server)
- **Backend**: http://localhost:8080 (Spring Boot application)

### Individual Services

#### Frontend (Angular)
```bash
# Start frontend only
npm run dev:frontend

# Build for production
npm run build:frontend

# Install frontend dependencies
npm run install:frontend
```

#### Backend (Spring Boot)
```bash
# Start backend only
npm run dev:backend

# Build for production
npm run build:backend

# Install backend dependencies (resolve Maven dependencies)
npm run install:backend
```

## 📁 Frontend Details

### Technology Stack
- **Angular 17** with standalone components
- **TypeScript** for type safety
- **SCSS** for styling
- **RxJS** for reactive programming

### Key Features
- Modern Angular architecture with standalone components
- Responsive design with gradient UI
- HTTP client for backend communication
- Routing with lazy loading

### API Integration
The frontend includes a test component that demonstrates connectivity with the Spring Boot backend through the `/api/hello` endpoint.

## 📁 Backend Details

### Technology Stack
- **Spring Boot 3.2.0**
- **Java 17**
- **H2 Database** (in-memory for development)
- **Spring Data JPA**
- **Maven** for dependency management

### API Endpoints

| Method | Endpoint | Description |
|--------|----------|-------------|
| GET | `/api/hello` | Returns a greeting message with timestamp |
| GET | `/api/status` | Returns service status information |

### Database
- **H2 Console**: http://localhost:8080/h2-console
- **JDBC URL**: `jdbc:h2:mem:testdb`
- **Username**: `sa`
- **Password**: `password`

## 🔧 Configuration

### CORS
The backend is configured to accept requests from the frontend running on `http://localhost:4200`.

### Environment Variables
You can override default configurations using environment variables or application properties.

## 🏃‍♂️ Building for Production

### Build Everything
```bash
npm run build
```

### Build Individual Services
```bash
# Frontend only
npm run build:frontend

# Backend only
npm run build:backend
```

## 📝 Development Workflow

1. **Start development servers**: `npm run dev`
2. **Make changes** to either frontend or backend
3. **Test the integration** using the frontend's "Test Backend Connection" button
4. **Build for production** when ready: `npm run build`

## 🐛 Troubleshooting

### Frontend Issues
- Ensure Node.js v18+ is installed
- Clear npm cache: `npm cache clean --force`
- Delete `node_modules` and reinstall: `rm -rf apps/frontend/node_modules && npm run install:frontend`

### Backend Issues
- Ensure Java 17+ is installed
- Check if port 8080 is available
- Verify Maven wrapper permissions: `chmod +x apps/backend/mvnw`

### CORS Issues
If you encounter CORS errors, ensure the backend's CORS configuration matches your frontend URL.

## 📦 Scripts Reference

| Command | Description |
|---------|-------------|
| `npm run dev` | Start both frontend and backend |
| `npm run dev:frontend` | Start only the frontend |
| `npm run dev:backend` | Start only the backend |
| `npm run build` | Build both applications |
| `npm run build:frontend` | Build only the frontend |
| `npm run build:backend` | Build only the backend |
| `npm run setup` | Install all dependencies |
| `npm run install:frontend` | Install frontend dependencies |
| `npm run install:backend` | Resolve backend dependencies |

## 🚀 Next Steps

- Add authentication and authorization
- Implement a database layer with persistent storage
- Add comprehensive testing suites
- Set up CI/CD pipelines
- Add Docker containers for deployment

## 📄 License

This project is licensed under the MIT License. 