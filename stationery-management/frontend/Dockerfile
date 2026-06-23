#Start building my Docker image from this existing image: 
    #nginx is the image name. Stagle indicates - use the stable version, Alpine (5-10 MB) is very lightweight Linux distribution 
FROM nginx:stable-alpine

#COPY files from my computer into the Docker image
#build/ = source folder
# /usr/... = destination inside the container
#Take my React build files and place them inside Nginx's website folder
COPY build/ /usr/share/nginx/html

#Copy nginx.conf and paste it in /etc/....
#In /etc/... we have default Nginx configurations
#So, we say replace default nginx configuration with MY custom nginx configuration
COPY nginx.conf /etc/nginx/conf.d/default.conf

#My application listens to this port: 
EXPOSE 80

#Run this command when container starts 
#nginx-> start nginx
#-g -> paste a global configuration directive
#daemon off -> keep the nginx running in the foreground. (Normally nginx runs in the background but docker does not like this, it expects one main process running in foreground. Otherwise Docker thinks application ended and container stops.)
CMD ["nginx", "-g", "daemon off;"]