import { HashRouter, Route, Routes } from 'react-router-dom';
import Shell from '@/components/Shell';
import Home from '@/pages/Home';
import Settings from '@/pages/Settings';

export default function App() {
  return (
    <HashRouter>
      <Routes>
        <Route element={<Shell />}>
          <Route path="/" element={<Home />} />
          <Route path="/settings" element={<Settings />} />
          <Route path="*" element={<Home />} />
        </Route>
      </Routes>
    </HashRouter>
  );
}
