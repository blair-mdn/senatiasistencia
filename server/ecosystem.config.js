module.exports = {
  apps: [
    {
      name: 'senati-asistencia-api',
      script: './dist/main.js',
      instances: 1,
      exec_mode: 'fork',
      env: {
        ...process.env,
        NODE_ENV: 'development',
        PORT: process.env.PORT || 3000
      },
      env_production: {
        ...process.env,
        NODE_ENV: 'production',
        PORT: process.env.PORT || 3000
      },
      log_date_format: 'YYYY-MM-DD HH:mm:ss',
      error_file: './logs/err.log',
      out_file: './logs/out.log',
      log_file: './logs/combined.log',
      time: true,
      autorestart: true,
      watch: false,
      max_memory_restart: '1G'
    }
  ]
};