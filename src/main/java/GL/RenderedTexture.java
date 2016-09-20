package GL;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

/**
 * Created by felix on 14.07.2016.
 */
public class RenderedTexture {

    public int framebufferID, textureID, renderbufferID;

    private int width, height;

    public RenderedTexture(int width, int height)
    {
        this.width=width;
        this.height=height;

        // Framebuffer
        framebufferID = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);

        // Texture to render to
        textureID = glGenTextures();
        // bind the newly created texture (all future texture functions will modify this texture)
        glBindTexture(GL_TEXTURE_2D, textureID);
        // give an empty image to OpenGL (the last "0")
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGB, this.width, this.height, 0, GL_RGB, GL_UNSIGNED_BYTE, 0);
        // poor filtering. Needed!
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_NEAREST);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_NEAREST);

        // Depth Buffer
        renderbufferID = glGenRenderbuffers();
        glBindRenderbuffer(GL_RENDERBUFFER, renderbufferID);
        glRenderbufferStorage(GL_RENDERBUFFER, GL_DEPTH_COMPONENT, width, height);
        glFramebufferRenderbuffer(GL_FRAMEBUFFER, GL_DEPTH_ATTACHMENT, GL_RENDERBUFFER, renderbufferID);

        // Configure Framebuffer
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, textureID, 0, 0);
        glDrawBuffer(GL_COLOR_ATTACHMENT0);

        // Check, whether everything went as planned
        if(glCheckFramebufferStatus(GL_FRAMEBUFFER) != GL_FRAMEBUFFER_COMPLETE)
            System.err.println("Rendered Texture: Framebuffer incomplete");

    }

    public void bind()
    {
        glBindFramebuffer(GL_FRAMEBUFFER, framebufferID);
        glViewport(0, 0, width, height);
    }

    public void unbind(int width, int height)
    {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
        glViewport(0, 0, width, height);
    }

}
