const express = require('express');
const cors = require('cors');
const bodyParser = require('body-parser');
const path = require('path');

const app = express();
const PORT = process.env.PORT || 3000;

app.use(cors());
app.use(bodyParser.json());
app.use(express.static(path.join(__dirname, 'public')));

// Mock Database
let courses = [
    {
        id: 1,
        title: "Advanced React: Mastery 2025",
        category: "Development",
        price: 89.99,
        instructorId: 'inst_1',
        instructor: "Priya Sharma",
        image: "https://images.unsplash.com/photo-1633356122544-f134324a6cee?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80",
        students: ['std_1'],
        status: 'active',
        modules: [
            { id: 1.1, title: 'Hooks & Context Depth', content: 'In-depth look at useEffect, useMemo, and Context API.' },
            { id: 1.2, title: 'State Management Strategy', content: 'Choosing between Redux, Zustand, and React Query.' },
            { id: 1.3, title: 'Performance Optimization', content: 'Code splitting, lazy loading, and profiling components.' },
            { id: 1.4, title: 'Component Library Design', content: 'Building a consistent and accessible UI library.' },
            { id: 1.5, title: 'Enterprise Architecture', content: 'Structuring large-scale React applications for growth.' }
        ]
    },
    {
        id: 2,
        title: "UI/UX Design for Bharat",
        category: "Design",
        price: 59.99,
        instructorId: 'inst_2',
        instructor: "Ishaan Verma",
        image: "https://images.unsplash.com/photo-1586717791821-3f44a563dc4c?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80",
        students: ['std_1'],
        status: 'active',
        modules: [
            { id: 2.1, title: 'Localization & Language', content: 'Designing for multilingual users across India.' },
            { id: 2.2, title: 'Accessibility (A11y)', content: 'Inclusive design for diverse physical and cognitive needs.' },
            { id: 2.3, title: 'Data Visualization', content: 'Representing complex data simply for retail users.' },
            { id: 2.4, title: 'High-Fidelity Prototyping', content: 'Creating realistic micro-interactions in Figma.' },
            { id: 2.5, title: 'Design to Dev Handoff', content: 'Effective communication with engineer teams.' }
        ]
    },
    {
        id: 3,
        title: "Full Stack Node.js & MongoDB",
        category: "Development",
        price: 79.99,
        instructorId: 'inst_1',
        instructor: "Priya Sharma",
        image: "https://images.unsplash.com/photo-1517694712202-14dd9538aa97?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80",
        students: ['std_1'],
        status: 'active',
        modules: [
            { id: 3.1, title: 'Node.js Core Foundations', content: 'Events, streams, and file systems.' },
            { id: 3.2, title: 'Express & Middleware', content: 'Routing and building custom middleware.' },
            { id: 3.3, title: 'MongoDB Data Modeling', content: 'Schemas, relations, and aggregation pipeline.' },
            { id: 3.4, title: 'RESTful API Security', content: 'JWT, hashing, and rate limiting.' },
            { id: 3.5, title: 'Cloud Deployment', content: 'Docker, AWS, and CI/CD pipelines.' }
        ]
    },
    {
        id: 4,
        title: "Digital Marketing for Startups",
        category: "Marketing",
        price: 49.99,
        instructorId: 'inst_3',
        instructor: "Amit Gupta",
        image: "https://images.unsplash.com/photo-1460925895917-afdab827c52f?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80",
        students: ['std_1'],
        status: 'active',
        modules: [
            { id: 4.1, title: 'Brand Storytelling', content: 'Developing a unique position.' },
            { id: 4.2, title: 'SEO & Content Engine', content: 'Ranking for relevant keywords.' },
            { id: 4.3, title: 'Paid Channels (SMM)', content: 'Running performance ads.' },
            { id: 4.4, title: 'Growth Hacking', content: 'A/B testing and funnel optimization.' },
            { id: 4.5, title: 'Analytics & Attribution', content: 'Measuring what matters.' }
        ]
    },
    {
        id: 5,
        title: "Cloud Architecture Patterns",
        category: "DevOps",
        price: 99.99,
        instructorId: 'inst_3',
        instructor: "Amit Gupta",
        image: "https://images.unsplash.com/photo-1451187580459-43490279c0fa?ixlib=rb-1.2.1&auto=format&fit=crop&w=400&q=80",
        students: [],
        status: 'pending',
        modules: [
            { id: 5.1, title: 'Serverless Fundamentals', content: 'Lambda, API Gateway, and S3.' },
            { id: 5.2, title: 'Distributed Systems', content: 'Microservices and event-driven design.' },
            { id: 5.3, title: 'Cloud Security Model', content: 'IAM, VPC, and Encryption.' },
            { id: 5.4, title: 'Kubernetes Mastery', content: 'Managing containerized fleets.' },
            { id: 5.5, title: 'Disaster Recovery', content: 'High availability and backup.' }
        ]
    }
];

let users = [
    { 
        id: 'std_1', 
        name: 'Arjun Kumar', 
        email: 'arjun@skillverse.in', 
        role: 'student', 
        enrolled: [1, 2, 3, 4], 
        completedModules: { 
            1: [1.1, 1.2, 1.3, 1.4, 1.5], 
            2: [2.1, 2.2, 2.3, 2.4, 2.5],
            3: [],
            4: []
        } 
    },
    { id: 'adm_1', name: 'Adithi Rao', email: 'admin@skillverse.in', role: 'admin' },
    { id: 'inst_1', name: 'Dr. Priya Sharma', email: 'priya@skillverse.in', role: 'instructor' },
    { id: 'inst_2', name: 'Ishaan Verma', email: 'ishaan@skillverse.in', role: 'instructor' },
    { id: 'inst_3', name: 'Dr. Amit Gupta', email: 'amit@skillverse.in', role: 'instructor' }
];

// Auth Endpoints
app.post('/api/login', (req, res) => {
    const { email, role } = req.body;
    const user = users.find(u => u.email === email && u.role === role);
    if (user) {
        res.json({ success: true, user });
    } else {
        res.status(401).json({ success: false, message: 'Invalid credentials or role' });
    }
});

app.post('/api/register', (req, res) => {
    const { name, email, role } = req.body;
    if (users.find(u => u.email === email)) {
        return res.status(400).json({ success: false, message: 'User already exists' });
    }
    const newUser = { id: `user_${Date.now()}`, name, email, role, enrolled: [], completedModules: {} };
    users.push(newUser);
    res.json({ success: true, user: newUser });
});

// Course Endpoints
app.get('/api/courses', (req, res) => res.json(courses));

app.post('/api/courses', (req, res) => {
    const newCourse = { ...req.body, id: courses.length + 1, students: [], status: 'pending' };
    courses.push(newCourse);
    res.status(201).json(newCourse);
});

app.delete('/api/courses/:id', (req, res) => {
    const id = parseInt(req.params.id);
    courses = courses.filter(c => c.id !== id);
    res.json({ success: true });
});

app.put('/api/courses/:id/status', (req, res) => {
    const id = parseInt(req.params.id);
    const { status } = req.body;
    const course = courses.find(c => c.id === id);
    if (course) {
        course.status = status;
        res.json(course);
    } else {
        res.status(404).json({ error: 'Course not found' });
    }
});

app.post('/api/courses/enroll', (req, res) => {
    const { courseId, userId } = req.body;
    const course = courses.find(c => c.id === courseId);
    const user = users.find(u => u.id === userId);
    if (course && user) {
        if (!course.students.includes(userId)) course.students.push(userId);
        if (!user.enrolled.includes(courseId)) user.enrolled.push(courseId);
        if (!user.completedModules[courseId]) user.completedModules[courseId] = [];
        res.json({ success: true, user });
    } else {
        res.status(400).json({ error: 'Invalid course or user' });
    }
});

app.post('/api/courses/complete-module', (req, res) => {
    const { courseId, moduleId, userId } = req.body;
    const user = users.find(u => u.id === userId);
    if (user && user.completedModules[courseId]) {
        if (!user.completedModules[courseId].includes(moduleId)) {
            user.completedModules[courseId].push(moduleId);
        }
        res.json({ success: true, user });
    } else {
        res.status(400).json({ error: 'Progress could not be saved' });
    }
});

app.get('/api/instructor/students/:courseId', (req, res) => {
    const id = parseInt(req.params.courseId);
    const course = courses.find(c => c.id === id);
    if (course) {
        const studentDetails = users.filter(u => course.students.includes(u.id));
        res.json(studentDetails);
    } else {
        res.status(404).json({ error: 'Course not found' });
    }
});

app.listen(PORT, () => console.log(`Backend running on http://localhost:${PORT}`));
