uniform mat4 transform;

attribute vec4 vertex;
attribute vec4 color;

varying vec4 vertexColor;

void main() {
  gl_Position = transform * vertex;
  vertexColor = color;  
}
