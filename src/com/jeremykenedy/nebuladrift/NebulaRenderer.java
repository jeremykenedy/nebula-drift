package com.jeremykenedy.nebuladrift;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

final class NebulaRenderer implements GLSurfaceView.Renderer {
    private static final String VERTEX_SHADER =
            "attribute vec2 aPosition; varying vec2 vUv; void"
                    + " main(){vUv=aPosition*.5+.5;gl_Position=vec4(aPosition,0.0,1.0);}";
    private static final String FRAGMENT_SHADER =
            "precision mediump float; varying vec2 vUv; uniform vec2 uResolution; uniform float"
                + " uTime; uniform float uSpeed; uniform float uDensity; uniform float uStars;"
                + " uniform float uBrightness; uniform float uTwinkle; uniform float uMeteors;"
                + " uniform float uForm; uniform vec3 uPrimary; uniform vec3 uSecondary; float"
                + " hash(vec2 p){return fract(sin(dot(p,vec2(127.1,311.7)))*43758.5453);} float"
                + " noise(vec2 p){vec2 i=floor(p),f=fract(p);f=f*f*(3.0-2.0*f);return"
                + " mix(mix(hash(i),hash(i+vec2(1.,0.)),f.x),mix(hash(i+vec2(0.,1.)),hash(i+vec2(1.,1.)),f.x),f.y);}"
                + " float fbm(vec2 p){float v=0.,a=.5;for(int"
                + " i=0;i<4;i++){v+=noise(p)*a;p=p*2.02+vec2(7.2,3.1);a*=.5;}return v;} void"
                + " main(){vec2 uv=vUv;float aspect=uResolution.x/uResolution.y;vec2"
                + " p=(uv-.5)*vec2(aspect,1.0);float t=uTime*uSpeed*.012;float shape=uForm;vec2"
                + " warp=vec2(fbm(p*2.4+vec2(t,-t*.6)),fbm(p*2.4+vec2(-t*.7,t*.8)));vec2"
                + " q=p+(.20+shape*.025)*(warp-.5);float clouds=fbm(q*3.1+vec2(t*.45,-t*.3));float"
                + " folds=fbm(q*7.0+warp*1.7-vec2(t*.35,t*.25));float"
                + " filament=fbm(q*12.0+warp*2.2+vec2(t*.2,-t*.3));float"
                + " core=exp(-dot(q*vec2(.72,1.25),q*vec2(.72,1.25))*2.8);float"
                + " band=1.0-smoothstep(.04,.42,abs(q.y+sin(q.x*3.2+warp.x*2.)*.12));if(shape>.5&&shape<1.5){float"
                + " pillars=1.-smoothstep(.08,.36,abs(fract(q.x*3.6+warp.y*.5)-.5));band=mix(band,pillars,.5);}"
                + " if(shape>1.5&&shape<2.5){core=exp(-length(q+vec2(.12,-.02))*5.);band=mix(band,core,.65);}"
                + " if(shape>2.5){band=1.-smoothstep(.08,.5,abs(length(q)-(.23+warp.x*.18)));core*=.45;}"
                + " float"
                + " vapor=smoothstep(.24,.79,clouds)*(.58+folds*.65);vapor*=mix(1.0,band,.28);vec3"
                + " col=vec3(.0015,.0025,.009)+uPrimary*(vapor*uDensity*.42)+uSecondary*(core*(.12+filament*.35));"
                + "col+=uPrimary*smoothstep(.58,.88,filament)*vapor*.18;vec2"
                + " grid=uv*vec2(aspect,1.)*vec2(190.,110.)*mix(.65,1.6,uStars);vec2"
                + " cell=floor(grid);vec2 local=fract(grid)-.5;float"
                + " star=step(1.0-uStars*.075,hash(cell));float"
                + " radius=mix(.007,.018,hash(cell+9.));float"
                + " point=1.-smoothstep(radius,radius+.008,length(local-(vec2(hash(cell+2.),hash(cell+3.))-.5)*.72));float"
                + " tw= mix(1.,.55+.45*sin(uTime*(1.5+hash(cell+4.)*3.)+hash(cell+5.)*6.28),uTwinkle);float"
                + " stars=star*point*tw*(.35+hash(cell+7.)*.8);col+=vec3(.58,.72,1.)*stars;float"
                + " dust=step(.992,hash(floor(uv*vec2(aspect,1.)*460.)));col+=vec3(.45,.58,.82)*dust*.09;float"
                + " meteorPhase=fract(uTime*.018*uSpeed+hash(vec2(11.,7.)));float"
                + " my=fract(hash(vec2(21.,13.))+meteorPhase*.58);float"
                + " mx=fract(hash(vec2(4.,19.))+meteorPhase*1.6);float"
                + " trail=exp(-pow((uv.y-(my+(uv.x-mx)*.42))/.003,2.))*smoothstep(0.,.35,uv.x-mx)*smoothstep(1.,.35,uv.x-mx);col+=vec3(.55,.72,1.)*trail*uMeteors*.95;float"
                + " vignette=1.-smoothstep(.35,1.0,length((uv-.5)*vec2(aspect,1.))*1.12);"
                + "col*=mix(.55,1.0,vignette)*uBrightness;col=pow(max(col,vec3(0.)),vec3(.88));gl_FragColor=vec4(col,1.);}";

    private static final float[] PALETTE_PRIMARY = {
        0.42f, 0.12f, 0.85f, 0.08f, 0.38f, 0.94f, 0.02f, 0.58f, 0.38f, 0.86f, 0.09f, 0.25f
    };
    private static final float[] PALETTE_SECONDARY = {
        0.98f, 0.30f, 0.60f, 0.15f, 0.83f, 0.92f, 0.20f, 0.92f, 0.66f, 1.0f, 0.42f, 0.64f
    };
    private final FloatBuffer vertices;
    private int program;
    private int position;
    private int resolution;
    private int time;
    private int speed;
    private int density;
    private int stars;
    private int brightness;
    private int twinkle;
    private int meteors;
    private int form;
    private int primary;
    private int secondary;
    private int width = 1;
    private int height = 1;
    private long startedAt;
    private volatile NebulaOptions options;

    NebulaRenderer(NebulaOptions options) {
        this.options = options;
        vertices = ByteBuffer.allocateDirect(8 * 4).order(ByteOrder.nativeOrder()).asFloatBuffer();
        vertices.put(new float[] {-1f, -1f, 1f, -1f, -1f, 1f, 1f, 1f}).position(0);
    }

    void configure(NebulaOptions newOptions) {
        options = newOptions;
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        GLES20.glClearColor(0f, 0f, 0f, 1f);
        program = link(VERTEX_SHADER, FRAGMENT_SHADER);
        position = GLES20.glGetAttribLocation(program, "aPosition");
        resolution = GLES20.glGetUniformLocation(program, "uResolution");
        time = GLES20.glGetUniformLocation(program, "uTime");
        speed = GLES20.glGetUniformLocation(program, "uSpeed");
        density = GLES20.glGetUniformLocation(program, "uDensity");
        stars = GLES20.glGetUniformLocation(program, "uStars");
        brightness = GLES20.glGetUniformLocation(program, "uBrightness");
        twinkle = GLES20.glGetUniformLocation(program, "uTwinkle");
        meteors = GLES20.glGetUniformLocation(program, "uMeteors");
        form = GLES20.glGetUniformLocation(program, "uForm");
        primary = GLES20.glGetUniformLocation(program, "uPrimary");
        secondary = GLES20.glGetUniformLocation(program, "uSecondary");
        startedAt = System.nanoTime();
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int w, int h) {
        width = Math.max(1, w);
        height = Math.max(1, h);
        GLES20.glViewport(0, 0, width, height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        NebulaOptions current = options;
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        GLES20.glUseProgram(program);
        vertices.position(0);
        GLES20.glEnableVertexAttribArray(position);
        GLES20.glVertexAttribPointer(position, 2, GLES20.GL_FLOAT, false, 0, vertices);
        GLES20.glUniform2f(resolution, width, height);
        GLES20.glUniform1f(time, (System.nanoTime() - startedAt) / 1_000_000_000f);
        GLES20.glUniform1f(speed, current.speed / 3.0f);
        GLES20.glUniform1f(density, current.density / 3.0f);
        GLES20.glUniform1f(stars, current.stars / 4.0f);
        GLES20.glUniform1f(brightness, current.brightness / 3.0f);
        GLES20.glUniform1f(twinkle, current.twinkle ? 1f : 0f);
        GLES20.glUniform1f(meteors, current.meteors ? 1f : 0f);
        GLES20.glUniform1f(form, current.form);
        int colorIndex = Math.min(current.palette, NebulaOptions.PALETTES.length - 1);
        GLES20.glUniform3f(
                primary,
                PALETTE_PRIMARY[colorIndex * 3],
                PALETTE_PRIMARY[colorIndex * 3 + 1],
                PALETTE_PRIMARY[colorIndex * 3 + 2]);
        GLES20.glUniform3f(
                secondary,
                PALETTE_SECONDARY[colorIndex * 3],
                PALETTE_SECONDARY[colorIndex * 3 + 1],
                PALETTE_SECONDARY[colorIndex * 3 + 2]);
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4);
        GLES20.glDisableVertexAttribArray(position);
    }

    private static int link(String vertexSource, String fragmentSource) {
        int vertex = compile(GLES20.GL_VERTEX_SHADER, vertexSource);
        int fragment = compile(GLES20.GL_FRAGMENT_SHADER, fragmentSource);
        int result = GLES20.glCreateProgram();
        GLES20.glAttachShader(result, vertex);
        GLES20.glAttachShader(result, fragment);
        GLES20.glLinkProgram(result);
        int[] status = new int[1];
        GLES20.glGetProgramiv(result, GLES20.GL_LINK_STATUS, status, 0);
        if (status[0] == 0) throw new IllegalStateException(GLES20.glGetProgramInfoLog(result));
        GLES20.glDeleteShader(vertex);
        GLES20.glDeleteShader(fragment);
        return result;
    }

    private static int compile(int type, String source) {
        int shader = GLES20.glCreateShader(type);
        GLES20.glShaderSource(shader, source);
        GLES20.glCompileShader(shader);
        int[] status = new int[1];
        GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0);
        if (status[0] == 0) throw new IllegalStateException(GLES20.glGetShaderInfoLog(shader));
        return shader;
    }
}
