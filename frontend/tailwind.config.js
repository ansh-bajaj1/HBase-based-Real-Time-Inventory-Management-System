export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Space Grotesk', 'sans-serif'],
        mono: ['IBM Plex Mono', 'monospace']
      },
      colors: {
        ink: '#11212d',
        ocean: '#2b6777',
        mist: '#c8d8e4',
        sand: '#f2efe9',
        coral: '#f25f5c'
      }
    }
  },
  plugins: []
};
