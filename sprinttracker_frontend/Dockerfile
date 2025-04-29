# Use an official Node.js runtime as a parent image
FROM node:18-alpine

# Set the working directory
WORKDIR /app

# Copy the project files
COPY . .

# Install dependencies
RUN npm install

# Build the application
RUN npm run build

# Expose the port the application runs on
EXPOSE 3000

# Run the application
CMD ["npm", "start"]