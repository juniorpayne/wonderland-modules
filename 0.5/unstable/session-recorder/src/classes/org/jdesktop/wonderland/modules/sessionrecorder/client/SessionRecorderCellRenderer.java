/**
 * Copyright (c) 2016, Envisiture Consulting, LLC, All Rights Reserved
 */
package org.jdesktop.wonderland.modules.sessionrecorder.client;

import com.jme.bounding.BoundingBox;
import com.jme.math.FastMath;
import com.jme.math.Quaternion;
import com.jme.math.Vector3f;
import com.jme.scene.CameraNode;
import com.jme.scene.Node;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.ByteBuffer;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.JComponent;
import javax.swing.SwingUtilities;
import org.jdesktop.mtgame.CameraComponent;
import org.jdesktop.mtgame.Entity;
import org.jdesktop.mtgame.RenderBuffer;
import org.jdesktop.mtgame.RenderUpdater;
import org.jdesktop.mtgame.TextureRenderBuffer;
import org.jdesktop.mtgame.WorldManager;
import org.jdesktop.wonderland.client.cell.Cell;
import org.jdesktop.wonderland.client.cell.asset.AssetUtils;
import org.jdesktop.wonderland.client.jme.ClientContextJME;
import org.jdesktop.wonderland.client.jme.artimport.DeployedModel;
import org.jdesktop.wonderland.client.jme.artimport.LoaderManager;
import org.jdesktop.wonderland.client.jme.cellrenderer.BasicRenderer;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentNode;
import org.jdesktop.wonderland.modules.contentrepo.common.ContentResource;

/**
 *
 * @author Abhishek Upadhyay <abhiit61@gmail.com>
 */
public class SessionRecorderCellRenderer extends BasicRenderer implements RenderUpdater {

    //Use 16:9 aspect ratio
    public static final float WIDTH = 1.6f; //x-extent
    public static final float HEIGHT = 0.9f; //y-extent
    private static final int IMAGE_HEIGHT = 360;
    private static final int IMAGE_WIDTH = 640;
    private static final int PREVIEW_HEIGHT = 173;
    private static final int PREVIEW_WIDTH = 345;
    private int imageCounter;
    private int frameCounter;
    private TextureRenderBuffer textureBuffer = null;
    private CaptureComponent captureComponent = null;
    private BufferedImage captureImage = null;
    private ExecutorService imageStreamingThreadPool = Executors.newFixedThreadPool(5);
    private boolean repairImageSource = false;
    private volatile int uploadedFiles = 1000000;
    private volatile boolean uploadDone = false;
    private static final Logger LOGGER = Logger.getLogger(SessionRecorderCellRenderer.class.getName());
    /**
     * boolean variable for not throwing the null pointer exception for directly
     * setting thecaptureComponent to null on removingCaptureComponent();
     */
    private boolean stopCapturing = true;

    public SessionRecorderCellRenderer(Cell cell) {
        super(cell);
    }

    /**
     * attach camera model to scene
     *
     * @param entity
     * @return
     */
    protected Node createSceneGraph(Entity entity) {
        LOGGER.log(Level.INFO, "{0}:createSceneGraph():Start", this.getClass().getName());
        /* Create the scene graph object*/
        Node root = new Node("Movie Recorder Root");
        attachRecordingDevice(root, entity);
        root.setModelBound(new BoundingBox());
        root.setLocalRotation(new Quaternion().fromAngleAxis(FastMath.PI / 2, new Vector3f(1, 0, 0)));
        root.setLocalScale(2);
        root.updateModelBound();
        root.setName("Cell_" + cell.getCellID() + ":" + cell.getName());
        LOGGER.log(Level.INFO, "{0}:createSceneGraph():End", this.getClass().getName());
        return root;
    }

    /**
     * load camera model & add it to sceneroot also setup render buffer to
     * capture images
     *
     * @param device
     * @param entity
     */
    private void attachRecordingDevice(Node device, Entity entity) {
        LOGGER.log(Level.INFO, "{0}:attachRecordingDevice():Start", this.getClass().getName());
        try {
            addCameraModel(device, entity);
            setupRenderBuffer(device);
        } catch (IOException ex) {
            Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:attachRecordingDevice():End", this.getClass().getName());
    }

    /**
     * creates a JComponent that renders the buffered image
     */
    public class CaptureComponent extends JComponent {

        public CaptureComponent() {
            setBorder(BorderFactory.createLineBorder(Color.black));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.setColor(Color.BLACK);
            if (captureImage != null) {
                g.drawImage(captureImage, 0, 0, null);
            }
        }
    }

    /**
     * creates the new capture component with the given height and width
     *
     * @param width
     * @param height
     */
    private void createCaptureComponent(int width, int height) {
        LOGGER.log(Level.INFO, "{0}:createCaptureComponent():Start", this.getClass().getName());
        captureComponent = new CaptureComponent();
        captureComponent.setPreferredSize(new Dimension(width, height));
        captureComponent.setMinimumSize(new Dimension(width, height));
        LOGGER.log(Level.INFO, "{0}:createCaptureComponent():End", this.getClass().getName());
    }

    /**
     * Setup render buffer at camera's position
     *
     * @param device
     */
    private void setupRenderBuffer(Node device) {
        LOGGER.log(Level.INFO, "{0}:setupRenderBuffer():Start", this.getClass().getName());
        WorldManager wm = ClientContextJME.getWorldManager();
        //Create the texture buffer
        textureBuffer = (TextureRenderBuffer) wm.getRenderManager().createRenderBuffer(RenderBuffer.Target.TEXTURE_2D, IMAGE_WIDTH, IMAGE_HEIGHT);
        textureBuffer.setIncludeOrtho((false));

        //Create a camera node
        CameraNode cn = new CameraNode("MyCamera", null);
        //Create a node for the camera
        Node cameraSG = new Node("cameraSG");
        //Attach the camera to the node
        cameraSG.attachChild(cn);
        //Rotate the camera through 180 degrees about the Y-axis
        float angleDegrees = 180;
        float angleRadians = (float) Math.toRadians(angleDegrees);
        Quaternion quat = new Quaternion().fromAngleAxis(angleRadians, new Vector3f(0, 1, 0));
        cameraSG.setLocalRotation(quat);
        cameraSG.setLocalRotation(new Quaternion().fromAngleAxis(-FastMath.PI / 2, new Vector3f(1, 0, 0)));
        //Translate the camera so it's in front of the model
        cameraSG.setLocalTranslation(0f, 0.5f, -0.5f);
        //Create a camera component
        CameraComponent cc = wm.getRenderManager().createCameraComponent(cameraSG, cn, IMAGE_WIDTH, IMAGE_HEIGHT, 45.0f, (float) IMAGE_WIDTH / (float) IMAGE_HEIGHT, 1f, 2000f, false);

        //Set the camera for the render buffer
        textureBuffer.setCameraComponent(cc);
        // Associated the texture buffer with the render manager, but keep it
        // off initially.
        wm.getRenderManager().addRenderBuffer(textureBuffer);
        textureBuffer.setRenderUpdater(this);

        device.attachChild(cameraSG);
        LOGGER.log(Level.INFO, "{0}:setupRenderBuffer():End", this.getClass().getName());
    }

    /**
     * get camera model and attach to scene root
     *
     * @param device
     * @param entity
     * @throws IOException
     */
    private void addCameraModel(Node device, Entity entity) throws IOException {
        LOGGER.log(Level.INFO, "{0}:addCameraModel():Start", this.getClass().getName());
        //Load the cameramodel and add it to the scenegraph
        LoaderManager manager = LoaderManager.getLoaderManager();
        URL url = AssetUtils.getAssetURL("wla://session-recorder/camera-lens/camera-lens.kmz.dep", this.getCell());
        DeployedModel dm = manager.getLoaderFromDeployment(url);
        Node cameraModel = dm.getModelLoader().loadDeployedModel(dm, entity);
        device.attachChild(cameraModel);
        LOGGER.log(Level.INFO, "{0}:addCameraModel():End", this.getClass().getName());
    }

    /**
     * capture image and stream to server
     *
     * @param o
     */
    public void update(Object o) {
        try {
            //for capturing images while recording audio + video
            if (((SessionRecorderCell) cell).isRecording()
                    && !((SessionRecorderCell) cell).getSessionRecordingData().isOnlyAudio()) {
                try {
                    BufferedImage outputImage = createBufferedImage(textureBuffer.getTextureData());
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ImageIO.write(outputImage, "jpg", baos);
                    InputStream is = new ByteArrayInputStream(baos.toByteArray());
                    String fileName = imageCounter + ".jpg";
                    imageStreamingThreadPool.execute(new StreamingImageThread(fileName, is));
                } catch (Exception ex) {
                    Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
                imageCounter++;
                frameCounter++;
            }

            //if stopCapturing = false, capture the buffered image and paint it in the captureComponent
            if (!stopCapturing) {
                BufferedImage outputImage = createBufferedImage(textureBuffer.getTextureData());
                captureImage = resizeImage(outputImage, PREVIEW_WIDTH, PREVIEW_HEIGHT);
                try {
                    SwingUtilities.invokeLater(new Runnable() {
                        public void run() {
                            if (captureComponent != null) {
                                captureComponent.repaint();
                            }
                        }
                    });
                } catch (Exception ex) {
                    Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
            } else {
                if (captureComponent != null) {
                    LOGGER.log(Level.INFO, "{0}:upadte(): setting captureComponent to null", this.getClass().getName());
                    captureComponent = null;
                    captureImage = null;
                }
            }
        } catch (Exception ex) {
            Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public boolean uploadDone() {
        return uploadDone;
    }

    /**
     * used to interrupt the threads in the threadpool
     */
    public void stopStreamingThread() {
        LOGGER.log(Level.INFO, "{0}:stopStreamingThread():Start", this.getClass().getName());
        try {
            imageStreamingThreadPool.shutdownNow();
            imageStreamingThreadPool.awaitTermination(10, TimeUnit.SECONDS);
            imageStreamingThreadPool = Executors.newFixedThreadPool(5);
        } catch (InterruptedException ex) {
            Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
        }
        LOGGER.log(Level.INFO, "{0}:stopStreamingThread():End", this.getClass().getName());
    }

    /**
     * thread to upload the image to the server from the user's machine
     */
    private class StreamingImageThread implements Runnable {

        private final String fileName;
        private final InputStream is;

        private StreamingImageThread(String fileName, InputStream is) {
            this.fileName = fileName;
            this.is = is;
        }

        public void run() {
            try {
                if (((SessionRecorderCell) cell).getImageDirThread() != null) {
                    ((SessionRecorderCell) cell).getImageDirThread().join();
                }
                ContentNode node = (ContentNode) ((SessionRecorderCell) cell).getImageDirThread()
                        .getImageDir().createChild(fileName, ContentNode.Type.RESOURCE);
                ((ContentResource) node).put(is);
                uploadedFiles++;
                LOGGER.log(Level.INFO, "uploadedFiles : {0}", uploadedFiles);
                LOGGER.log(Level.INFO, "imageCounter : {0}", imageCounter);
                if (uploadedFiles == imageCounter) {
                    uploadDone = true;
                }
            } catch (Exception ex) {
                if (ex instanceof InterruptedException) {
                    Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
                } else {
                    //need to repair the image
                    uploadedFiles++;
                    repairImageSource = true;
                    LOGGER.log(Level.WARNING, "---exception for fileName : {0}", fileName);
                    Logger.getLogger(SessionRecorderCellRenderer.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }

    }

    /**
     * create buffered image from capture graphics
     *
     * @param bb
     * @return
     */
    private BufferedImage createBufferedImage(ByteBuffer bb) {
        int width = textureBuffer.getWidth();
        int height = textureBuffer.getHeight();

        bb.rewind();
        BufferedImage bi = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                int index = (y * width + x) * 3;
                int b = bb.get(index);
                int g = bb.get(index + 1);
                int r = bb.get(index + 2);

                int pixel = ((r & 255) << 16) | ((g & 255) << 8) | ((b & 255)) | 0xff000000;

                bi.setRGB(x, (height - y) - 1, pixel);
            }
        }
        return (bi);
    }

    /**
     * resize the buffered image to the given dimensions
     *
     * @param image
     * @param width
     * @param height
     * @return
     */
    public BufferedImage resizeImage(BufferedImage image, int width, int height) {
        int type = 0;
        type = image.getType();
        BufferedImage resizedImage = new BufferedImage(width, height, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(image, 0, 0, width, height, null);
        g.dispose();
        return resizedImage;
    }

    /**
     *
     * @return the value of the frame counter
     */
    int getFrameCounter() {
        return frameCounter;
    }

    /**
     * Reset the counters that are used for capturing the images
     */
    void resetImageCounter() {
        imageCounter = 1000000;
        uploadedFiles = 1000000;
        uploadDone = false;
    }

    /**
     * Reset the field that counts the number of frames captured.
     */
    void resetFrameCounter() {
        frameCounter = 0;
    }

    /**
     * @return whether image needs to be repaired
     */
    boolean needRepairImageSource() {
        return repairImageSource;
    }

    /**
     * @return returns the captureComponent and sets the stop capturing to
     * false, so the image can be rendered on this JComponent
     */
    JComponent getCaptureComponent() {
        if (captureComponent == null) {
            createCaptureComponent(PREVIEW_WIDTH, PREVIEW_HEIGHT);
        }
        stopCapturing = false;
        return captureComponent;
    }

    /**
     * this method, when called, helps in stopping the capturing the buffered
     * image and set the captured component to null
     */
    void removeCaptureComponent() {
        stopCapturing = true;
    }
}
