import { BrowserRouter, Routes, Route, Navigate } from 'react-router-dom';
import MainPage from './mainPage/MainPage';
import LoginPage from './loginPage/LoginPage';
import SignUpPage from './signUpPage/SignUpPage';

function App() {
  return (
    <BrowserRouter>
      <Routes>
        <Route path="/" element={<Navigate to="/login" />} />
        <Route path="/mails/:folder" element={<MainPage />} />
        <Route path="/mails/label/:folder" element={<MainPage />} />
        <Route path="/login" element={<LoginPage />} />
        <Route path="/signup" element={<SignUpPage />} />
      </Routes>
    </BrowserRouter>
  );
}

export default App;

