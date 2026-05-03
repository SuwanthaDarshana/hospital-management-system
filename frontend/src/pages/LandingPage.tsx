import { Link } from 'react-router-dom';
import {
  Heart, Shield, Users, Star, Phone, Mail, MapPin,
  ChevronRight, Activity, Stethoscope, Calendar, Award,
  CheckCircle, ArrowRight, Menu, X,
} from 'lucide-react';
import { useState } from 'react';

const STATS = [
  { value: '15,000+', label: 'Patients Served' },
  { value: '200+',    label: 'Expert Doctors' },
  { value: '25+',     label: 'Years of Excellence' },
  { value: '98%',     label: 'Patient Satisfaction' },
];

const SERVICES = [
  {
    icon: <Stethoscope size={28} />,
    title: 'General Consultation',
    desc: 'Expert physicians available for comprehensive health evaluations and personalized treatment plans.',
    color: 'bg-blue-50 text-blue-600',
  },
  {
    icon: <Activity size={28} />,
    title: 'Emergency Care',
    desc: '24/7 emergency services with state-of-the-art facilities and rapid response medical teams.',
    color: 'bg-red-50 text-red-600',
  },
  {
    icon: <Heart size={28} />,
    title: 'Cardiology',
    desc: 'Advanced cardiac care including diagnostics, interventional procedures, and rehabilitation.',
    color: 'bg-pink-50 text-pink-600',
  },
  {
    icon: <Shield size={28} />,
    title: 'Preventive Care',
    desc: 'Proactive health screenings, vaccinations, and wellness programs to keep you ahead of illness.',
    color: 'bg-green-50 text-green-600',
  },
  {
    icon: <Calendar size={28} />,
    title: 'Online Appointments',
    desc: 'Book, reschedule, or cancel appointments anytime from any device — no waiting on hold.',
    color: 'bg-purple-50 text-purple-600',
  },
  {
    icon: <Award size={28} />,
    title: 'Specialist Referrals',
    desc: 'Seamless coordination with over 50 specialties ensuring you see exactly the right expert.',
    color: 'bg-orange-50 text-orange-600',
  },
];

const DOCTORS = [
  {
    name: 'Dr. Anika Perera',
    specialty: 'Cardiologist',
    experience: '18 years',
    rating: 4.9,
    image: 'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=400&q=80',
  },
  {
    name: 'Dr. Roshan Silva',
    specialty: 'Neurologist',
    experience: '14 years',
    rating: 4.8,
    image: 'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=400&q=80',
  },
  {
    name: 'Dr. Samanthi Fernando',
    specialty: 'Pediatrician',
    experience: '12 years',
    rating: 4.9,
    image: 'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=400&q=80',
  },
];

const TESTIMONIALS = [
  {
    name: 'Kasun Jayawardena',
    text: 'The online appointment system is incredibly easy to use. I booked my cardiology consultation in under two minutes and got seen the very next day.',
    rating: 5,
    role: 'Patient since 2021',
    avatar: 'https://images.unsplash.com/photo-1507003211169-0a1dd7228f2d?w=100&q=80',
  },
  {
    name: 'Nimesha Bandara',
    text: 'Outstanding care from the emergency team. They were professional, fast, and compassionate throughout my entire treatment.',
    rating: 5,
    role: 'Patient since 2020',
    avatar: 'https://images.unsplash.com/photo-1438761681033-6461ffad8d80?w=100&q=80',
  },
  {
    name: 'Dinesh Wijesinghe',
    text: 'My family has been coming here for years. The doctors genuinely listen and explain everything clearly. We trust this hospital completely.',
    rating: 5,
    role: 'Patient since 2018',
    avatar: 'https://images.unsplash.com/photo-1500648767791-00dcc994a43e?w=100&q=80',
  },
];

const STEPS = [
  { step: '01', title: 'Create Account', desc: 'Register in minutes with your basic details.' },
  { step: '02', title: 'Find a Doctor',   desc: 'Browse specialists by department or condition.' },
  { step: '03', title: 'Book Appointment', desc: 'Pick a date and time that works for you.' },
  { step: '04', title: 'Get Treated',     desc: 'Visit the clinic or connect virtually.' },
];

export default function LandingPage() {
  const [menuOpen, setMenuOpen] = useState(false);

  return (
    <div className="font-sans text-gray-900 overflow-x-hidden">

      {/* ── Navbar ── */}
      <nav className="fixed top-0 inset-x-0 z-50 bg-white/90 backdrop-blur border-b border-gray-100 shadow-sm">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="flex h-16 items-center justify-between">

            <div className="flex items-center gap-2">
              <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary-600">
                <Heart size={18} className="text-white" />
              </div>
              <span className="text-xl font-bold text-gray-900">MediCare<span className="text-primary-600">+</span></span>
            </div>

            <div className="hidden md:flex items-center gap-8">
              {['Services', 'Doctors', 'About', 'Contact'].map(item => (
                <a key={item} href={`#${item.toLowerCase()}`}
                   className="text-sm font-medium text-gray-600 hover:text-primary-600 transition-colors">
                  {item}
                </a>
              ))}
            </div>

            <div className="hidden md:flex items-center gap-3">
              <Link to="/login"
                    className="text-sm font-medium text-gray-700 hover:text-primary-600 transition-colors px-4 py-2">
                Sign in
              </Link>
              <Link to="/register"
                    className="btn-primary text-sm px-5 py-2">
                Get Started
              </Link>
            </div>

            <button className="md:hidden p-2 text-gray-600" onClick={() => setMenuOpen(o => !o)}>
              {menuOpen ? <X size={22} /> : <Menu size={22} />}
            </button>
          </div>
        </div>

        {menuOpen && (
          <div className="md:hidden bg-white border-t border-gray-100 px-4 py-4 space-y-3">
            {['Services', 'Doctors', 'About', 'Contact'].map(item => (
              <a key={item} href={`#${item.toLowerCase()}`}
                 className="block text-sm font-medium text-gray-700 py-1"
                 onClick={() => setMenuOpen(false)}>
                {item}
              </a>
            ))}
            <div className="pt-2 flex flex-col gap-2">
              <Link to="/login"   className="btn-secondary text-sm w-full justify-center">Sign in</Link>
              <Link to="/register" className="btn-primary  text-sm w-full justify-center">Get Started</Link>
            </div>
          </div>
        )}
      </nav>

      {/* ── Hero ── */}
      <section className="relative min-h-screen flex items-center pt-16 bg-gradient-to-br from-primary-50 via-white to-blue-50">
        <div className="absolute inset-0 overflow-hidden pointer-events-none">
          <div className="absolute -top-32 -right-32 w-[600px] h-[600px] rounded-full bg-primary-100/60 blur-3xl" />
          <div className="absolute bottom-0 -left-20 w-[400px] h-[400px] rounded-full bg-blue-100/40 blur-3xl" />
        </div>

        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-20 grid lg:grid-cols-2 gap-16 items-center relative">
          <div>
            <span className="inline-flex items-center gap-2 bg-primary-100 text-primary-700 text-xs font-semibold px-3 py-1.5 rounded-full mb-6">
              <span className="w-2 h-2 rounded-full bg-primary-500 animate-pulse" />
              Sri Lanka's Leading Digital Healthcare Platform
            </span>
            <h1 className="text-5xl lg:text-6xl font-extrabold leading-tight tracking-tight text-gray-900 mb-6">
              Your Health,<br />
              <span className="text-primary-600">Our Priority</span>
            </h1>
            <p className="text-lg text-gray-500 leading-relaxed mb-8 max-w-lg">
              Book appointments with top specialists, manage your health records, and receive
              world-class care — all from the comfort of your home.
            </p>
            <div className="flex flex-wrap gap-4">
              <Link to="/register" className="btn-primary gap-2 px-7 py-3 text-base">
                Get Started Free <ArrowRight size={18} />
              </Link>
              <a href="#services" className="btn-secondary gap-2 px-7 py-3 text-base">
                Our Services <ChevronRight size={18} />
              </a>
            </div>

            <div className="mt-10 flex items-center gap-4">
              <div className="flex -space-x-2">
                {[
                  'https://images.unsplash.com/photo-1612349317150-e413f6a5b16d?w=60&q=80',
                  'https://images.unsplash.com/photo-1594824476967-48c8b964273f?w=60&q=80',
                  'https://images.unsplash.com/photo-1559839734-2b71ea197ec2?w=60&q=80',
                ].map((src, i) => (
                  <img key={i} src={src} alt="doctor"
                       className="w-10 h-10 rounded-full border-2 border-white object-cover" />
                ))}
              </div>
              <div>
                <div className="flex text-yellow-400 mb-0.5">
                  {[...Array(5)].map((_, i) => <Star key={i} size={14} fill="currentColor" />)}
                </div>
                <p className="text-xs text-gray-500">Trusted by <span className="font-semibold text-gray-700">15,000+</span> patients</p>
              </div>
            </div>
          </div>

          <div className="relative hidden lg:block">
            <div className="relative rounded-3xl overflow-hidden shadow-2xl h-[520px]">
              <img
                src="https://images.unsplash.com/photo-1576091160399-112ba8d25d1d?w=800&q=80"
                alt="Modern hospital"
                className="w-full h-full object-cover"
              />
              <div className="absolute inset-0 bg-gradient-to-t from-primary-900/30 to-transparent" />
            </div>

            <div className="absolute -bottom-6 -left-8 bg-white rounded-2xl shadow-xl p-4 flex items-center gap-3 border border-gray-100">
              <div className="w-10 h-10 rounded-full bg-green-100 flex items-center justify-center">
                <CheckCircle size={20} className="text-green-600" />
              </div>
              <div>
                <p className="text-xs text-gray-500">Appointment Confirmed</p>
                <p className="text-sm font-semibold text-gray-800">Dr. Anika Perera • Today 3 PM</p>
              </div>
            </div>

            <div className="absolute top-8 -right-6 bg-white rounded-2xl shadow-xl p-4 border border-gray-100">
              <div className="flex items-center gap-2 mb-1">
                <Activity size={16} className="text-primary-600" />
                <span className="text-xs font-semibold text-gray-700">Live Queue</span>
              </div>
              <p className="text-2xl font-bold text-primary-600">3 min</p>
              <p className="text-xs text-gray-400">Average wait time</p>
            </div>
          </div>
        </div>
      </section>

      {/* ── Stats ── */}
      <section className="bg-primary-600">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-14 grid grid-cols-2 lg:grid-cols-4 gap-8">
          {STATS.map(s => (
            <div key={s.label} className="text-center">
              <p className="text-4xl font-extrabold text-white mb-1">{s.value}</p>
              <p className="text-primary-200 text-sm font-medium">{s.label}</p>
            </div>
          ))}
        </div>
      </section>

      {/* ── Services ── */}
      <section id="services" className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <span className="text-primary-600 text-sm font-semibold uppercase tracking-widest">What We Offer</span>
            <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">Comprehensive Healthcare Services</h2>
            <p className="text-gray-500 max-w-xl mx-auto">
              From routine check-ups to specialised care, we cover every aspect of your health journey.
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {SERVICES.map(s => (
              <div key={s.title}
                   className="group rounded-2xl border border-gray-100 p-7 hover:shadow-lg hover:-translate-y-1 transition-all duration-300 cursor-pointer">
                <div className={`w-14 h-14 rounded-xl ${s.color} flex items-center justify-center mb-5 group-hover:scale-110 transition-transform`}>
                  {s.icon}
                </div>
                <h3 className="text-lg font-semibold text-gray-900 mb-2">{s.title}</h3>
                <p className="text-sm text-gray-500 leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── About / Why Us ── */}
      <section id="about" className="py-24 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 grid lg:grid-cols-2 gap-16 items-center">
          <div className="relative">
            <div className="rounded-3xl overflow-hidden shadow-xl h-[460px]">
              <img
                src="https://images.unsplash.com/photo-1551601651-2a8555f1a136?w=700&q=80"
                alt="Medical team"
                className="w-full h-full object-cover"
              />
            </div>
            <div className="absolute -bottom-6 -right-6 bg-white rounded-2xl shadow-xl p-5 border border-gray-100 max-w-[180px]">
              <Award size={28} className="text-primary-600 mb-2" />
              <p className="text-2xl font-extrabold text-gray-900">ISO 9001</p>
              <p className="text-xs text-gray-500 mt-1">Certified Quality Management</p>
            </div>
          </div>

          <div>
            <span className="text-primary-600 text-sm font-semibold uppercase tracking-widest">Why Choose Us</span>
            <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-6 leading-tight">
              Care You Can<br />Always Count On
            </h2>
            <p className="text-gray-500 mb-8 leading-relaxed">
              We combine world-class medical expertise with cutting-edge technology to deliver
              healthcare that is accessible, transparent, and patient-first.
            </p>
            <ul className="space-y-4">
              {[
                'Board-certified specialists across 50+ medical fields',
                'Digital health records accessible anytime, anywhere',
                'Real-time appointment management and reminders',
                'Secure, HIPAA-compliant data protection',
                'Multilingual support for all patients',
              ].map(item => (
                <li key={item} className="flex items-start gap-3">
                  <CheckCircle size={20} className="text-primary-600 flex-shrink-0 mt-0.5" />
                  <span className="text-gray-600 text-sm">{item}</span>
                </li>
              ))}
            </ul>
            <Link to="/register" className="btn-primary mt-10 inline-flex gap-2">
              Join MediCare+ Today <ArrowRight size={16} />
            </Link>
          </div>
        </div>
      </section>

      {/* ── Doctors ── */}
      <section id="doctors" className="py-24 bg-white">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <span className="text-primary-600 text-sm font-semibold uppercase tracking-widest">Our Team</span>
            <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">Meet Our Specialists</h2>
            <p className="text-gray-500 max-w-xl mx-auto">
              Our doctors are leaders in their fields, committed to delivering evidence-based, compassionate care.
            </p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-8">
            {DOCTORS.map(d => (
              <div key={d.name}
                   className="group rounded-2xl border border-gray-100 overflow-hidden hover:shadow-xl transition-all duration-300 hover:-translate-y-1">
                <div className="h-64 overflow-hidden bg-gray-100">
                  <img src={d.image} alt={d.name}
                       className="w-full h-full object-cover group-hover:scale-105 transition-transform duration-500" />
                </div>
                <div className="p-6">
                  <div className="flex items-center justify-between mb-1">
                    <h3 className="font-semibold text-gray-900">{d.name}</h3>
                    <span className="flex items-center gap-1 text-xs font-medium text-yellow-600 bg-yellow-50 px-2 py-0.5 rounded-full">
                      <Star size={12} fill="currentColor" /> {d.rating}
                    </span>
                  </div>
                  <p className="text-primary-600 text-sm font-medium mb-1">{d.specialty}</p>
                  <p className="text-gray-400 text-xs">{d.experience} experience</p>
                  <Link to="/register"
                        className="mt-4 w-full btn-secondary text-sm justify-center">
                    Book Appointment
                  </Link>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── How It Works ── */}
      <section className="py-24 bg-gradient-to-br from-primary-600 to-primary-800 relative overflow-hidden">
        <div className="absolute inset-0 pointer-events-none">
          <div className="absolute top-0 left-1/4 w-96 h-96 rounded-full bg-white/5 blur-3xl" />
          <div className="absolute bottom-0 right-1/4 w-80 h-80 rounded-full bg-white/5 blur-3xl" />
        </div>
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 relative">
          <div className="text-center mb-14">
            <span className="text-primary-200 text-sm font-semibold uppercase tracking-widest">Simple Process</span>
            <h2 className="text-4xl font-extrabold text-white mt-2 mb-4">How It Works</h2>
            <p className="text-primary-200 max-w-lg mx-auto">Get started with quality care in four easy steps.</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-6">
            {STEPS.map((s, i) => (
              <div key={s.step} className="relative text-center">
                {i < STEPS.length - 1 && (
                  <div className="hidden lg:block absolute top-8 left-[60%] w-full h-px bg-white/20 border-dashed border-t border-white/30" />
                )}
                <div className="w-16 h-16 rounded-2xl bg-white/15 border border-white/20 flex items-center justify-center text-2xl font-extrabold text-white mx-auto mb-5">
                  {s.step}
                </div>
                <h3 className="text-white font-semibold mb-2">{s.title}</h3>
                <p className="text-primary-200 text-sm leading-relaxed">{s.desc}</p>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── Testimonials ── */}
      <section className="py-24 bg-gray-50">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8">
          <div className="text-center mb-14">
            <span className="text-primary-600 text-sm font-semibold uppercase tracking-widest">Patient Stories</span>
            <h2 className="text-4xl font-extrabold text-gray-900 mt-2 mb-4">What Our Patients Say</h2>
            <p className="text-gray-500 max-w-lg mx-auto">Real experiences from real patients who trust us with their health.</p>
          </div>
          <div className="grid sm:grid-cols-2 lg:grid-cols-3 gap-6">
            {TESTIMONIALS.map(t => (
              <div key={t.name} className="bg-white rounded-2xl p-7 shadow-sm border border-gray-100 hover:shadow-md transition-shadow">
                <div className="flex text-yellow-400 mb-4">
                  {[...Array(t.rating)].map((_, i) => <Star key={i} size={16} fill="currentColor" />)}
                </div>
                <p className="text-gray-600 text-sm leading-relaxed mb-6">"{t.text}"</p>
                <div className="flex items-center gap-3 pt-4 border-t border-gray-100">
                  <img src={t.avatar} alt={t.name}
                       className="w-11 h-11 rounded-full object-cover" />
                  <div>
                    <p className="text-sm font-semibold text-gray-900">{t.name}</p>
                    <p className="text-xs text-gray-400">{t.role}</p>
                  </div>
                </div>
              </div>
            ))}
          </div>
        </div>
      </section>

      {/* ── CTA ── */}
      <section className="py-20 bg-white">
        <div className="max-w-4xl mx-auto px-4 sm:px-6 lg:px-8 text-center">
          <div className="bg-gradient-to-br from-primary-600 to-primary-800 rounded-3xl px-8 py-16 shadow-2xl relative overflow-hidden">
            <div className="absolute inset-0 pointer-events-none">
              <div className="absolute -top-20 -right-20 w-64 h-64 rounded-full bg-white/10 blur-2xl" />
              <div className="absolute -bottom-20 -left-20 w-64 h-64 rounded-full bg-white/10 blur-2xl" />
            </div>
            <div className="relative">
              <h2 className="text-4xl font-extrabold text-white mb-4">
                Start Your Health Journey Today
              </h2>
              <p className="text-primary-200 text-lg mb-8 max-w-xl mx-auto">
                Join thousands of patients who have transformed how they manage their health with MediCare+.
              </p>
              <div className="flex flex-wrap gap-4 justify-center">
                <Link to="/register" className="bg-white text-primary-700 font-semibold px-8 py-3 rounded-lg hover:bg-primary-50 transition-colors inline-flex items-center gap-2">
                  Create Free Account <ArrowRight size={16} />
                </Link>
                <Link to="/login" className="border border-white/40 text-white font-semibold px-8 py-3 rounded-lg hover:bg-white/10 transition-colors">
                  Sign In
                </Link>
              </div>
            </div>
          </div>
        </div>
      </section>

      {/* ── Footer ── */}
      <footer id="contact" className="bg-gray-900 text-gray-400">
        <div className="max-w-7xl mx-auto px-4 sm:px-6 lg:px-8 py-16">
          <div className="grid sm:grid-cols-2 lg:grid-cols-4 gap-10 mb-12">
            <div>
              <div className="flex items-center gap-2 mb-4">
                <div className="flex h-9 w-9 items-center justify-center rounded-xl bg-primary-600">
                  <Heart size={18} className="text-white" />
                </div>
                <span className="text-xl font-bold text-white">MediCare<span className="text-primary-400">+</span></span>
              </div>
              <p className="text-sm leading-relaxed mb-5">
                Providing compassionate, world-class healthcare to every patient — wherever they are.
              </p>
              <div className="space-y-2 text-sm">
                <p className="flex items-center gap-2"><Phone size={14} className="text-primary-400" /> +94 11 234 5678</p>
                <p className="flex items-center gap-2"><Mail size={14} className="text-primary-400" /> info@medicare-plus.lk</p>
                <p className="flex items-center gap-2"><MapPin size={14} className="text-primary-400" /> Colombo 07, Sri Lanka</p>
              </div>
            </div>

            {[
              {
                title: 'Services',
                links: ['General Consultation', 'Emergency Care', 'Cardiology', 'Pediatrics', 'Neurology'],
              },
              {
                title: 'Quick Links',
                links: ['About Us', 'Our Doctors', 'Appointments', 'Patient Portal', 'Careers'],
              },
              {
                title: 'Support',
                links: ['Help Center', 'Privacy Policy', 'Terms of Service', 'Cookie Policy', 'Contact Us'],
              },
            ].map(col => (
              <div key={col.title}>
                <h4 className="text-white font-semibold mb-4">{col.title}</h4>
                <ul className="space-y-2 text-sm">
                  {col.links.map(link => (
                    <li key={link}>
                      <a href="#" className="hover:text-primary-400 transition-colors">{link}</a>
                    </li>
                  ))}
                </ul>
              </div>
            ))}
          </div>

          <div className="border-t border-gray-800 pt-8 flex flex-col sm:flex-row items-center justify-between gap-4 text-xs">
            <p>© {new Date().getFullYear()} MediCare+. All rights reserved.</p>
            <div className="flex items-center gap-2">
              <Users size={14} className="text-primary-400" />
              <span>15,000+ patients trust MediCare+</span>
            </div>
          </div>
        </div>
      </footer>
    </div>
  );
}
