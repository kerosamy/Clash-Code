import { Routes, Route, Navigate } from 'react-router-dom';
import ProfileNav from "../components/profile/ProfileNav";
import { profileSubRoutes } from '../routes/routes.config';

export default function Profile() {
    return (
        <div className="flex flex-col min-h-screen font-anta">
            <ProfileNav />
            
            <div className="flex-1 bg-background">
                <Routes>
                    {profileSubRoutes.map(({ path, component: Component }) => (
                        <Route 
                            key={path || 'index'} 
                            path={path} 
                            element={<Component />} 
                        />
                    ))}
                    <Route path="*" element={<Navigate to="/profile" replace />} />
                </Routes>
            </div>
        </div>
    );
}