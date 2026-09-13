/** @type {import('tailwindcss').Config} */
export default {
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  darkMode: 'class',
  theme: {
    extend: {
      colors: {
        calc: {
          primary: 'var(--color-primary)',
          surface: 'var(--color-surface)',
          background: 'var(--color-background)',
          display: 'var(--color-display)',
        }
      }
    },
  },
  plugins: [],
}
