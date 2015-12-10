#ifdef GL_ES
#define LOWP lowp
precision mediump float;
#else
#define LOWP
#endif

varying LOWP vec4 vColor;
varying vec2 vTexCoord;

uniform sampler2D u_texture;
uniform mat4 u_projTrans;

void main() {
	vec4 DiffuseColor = texture2D(u_texture, vTexCoord);
	//float gray = dot(DiffuseColor.rgb, vec3(0.299, 0.587, 0.114));
    DiffuseColor.rgb = mix(DiffuseColor.rgb, vec3(0.3, 0.3, 0.3) , 0.4);
    gl_FragColor = DiffuseColor * vColor;

}