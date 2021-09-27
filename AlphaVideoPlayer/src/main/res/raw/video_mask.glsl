//#extension GL_OES_EGL_image_external : require

precision mediump float;

//varying mediump vec2 textureCoordinate;

//uniform samplerExternalOES inputImageTexture;

varying mediump vec2 textureCoordinate;

uniform sampler2D inputImageTexture;

uniform mediump float isleft;

void main(){

    lowp vec4 centralColor = texture2D(inputImageTexture, textureCoordinate);
    lowp vec4 sampleColor = texture2D(inputImageTexture, vec2(textureCoordinate.x +0.5 *isleft, textureCoordinate.y));

    lowp float newAlpha = dot(sampleColor.rgb, vec3(.33333334, .33333334, .33333334))*sampleColor.a;

    gl_FragColor = vec4(centralColor.xyz, newAlpha);

}