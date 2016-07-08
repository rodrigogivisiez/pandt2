import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

/**
 * Created by SiongLeng on 29/1/2016.
 */
public class MainForm extends JFrame {
    private static MainForm _this;
    private static Container pane;
    private static final int IMAGE_WIDTH = 600;
    private static final int IMAGE_HEIGHT = 400;
    private static  MyCanvas canvas, mirrorCanvas;
    private static int startingX, startingY;
    private static ArrayList<MyCanvas> canvases, mirrorCanvases;
    private static String fileName1, fileName2;
    private static FireDB fireDB;

    public static void main(String[] args) {
        //Schedule a job for the event-dispatching thread:
        //creating and showing this application's GUI.

        fireDB = new FireDB();
        canvases = new ArrayList<MyCanvas>();
        mirrorCanvases = new ArrayList<MyCanvas>();
        MainForm myForm = new MainForm();

    }

    public MainForm() {
        super("Loading...");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1215,430);
        setResizable(false);
        pane = getContentPane();
        pane.setLayout(null);
        _this = this;
        refreshImageCount();
        resetEverything();


        setVisible(true);

        this.addKeyListener(new KeyListener() {

            @Override
            public void keyTyped(KeyEvent e) {
            }

            @Override
            public void keyPressed(KeyEvent e) {
                if ((e.getKeyCode() == KeyEvent.VK_S) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    save();
                }
                if ((e.getKeyCode() == KeyEvent.VK_Z) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    popCanvas();
                }
                if ((e.getKeyCode() == KeyEvent.VK_R) && ((e.getModifiers() & KeyEvent.CTRL_MASK) != 0)) {
                    resetEverything();
                }

            }

            @Override
            public void keyReleased(KeyEvent e) {
            }
        });

    }

    public static void save(){

        final boolean[] cancel = {false};

        if(canvases.size() != 5){
            JOptionPane.showMessageDialog(_this, "Please pick 5 differences before save!");
            return;
        }

        final ImageDetails imageDetails = new ImageDetails();

        int i = 1;
        for(MyCanvas canvas : canvases){
            CorrectArea correctArea = new CorrectArea();
            correctArea.setTopLeftX(canvas.getStartX());
            correctArea.setTopLeftY(canvas.getStartY());
            correctArea.setBottomRightX(canvas.getFinalX());
            correctArea.setBottomRightY(canvas.getFinalY());

            if(i == 1) imageDetails.setArea1(correctArea);
            else if(i == 2) imageDetails.setArea2(correctArea);
            else if(i == 3) imageDetails.setArea3(correctArea);
            else if(i == 4) imageDetails.setArea4(correctArea);
            else if(i == 5) imageDetails.setArea5(correctArea);

            i++;
        }

        imageDetails.setWidth(IMAGE_WIDTH);
        imageDetails.setHeight(IMAGE_HEIGHT);


        JOptionPane opt = new JOptionPane("Saving Item, please don't close this window...");
        final JDialog dlg = opt.createDialog("Saving");

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String key = fireDB.getKey();
                imageDetails.setId(key);
                resizeFile(fileName1, 1);
                resizeFile(fileName2, 2);

                File f = new File("1.jpg");
                File f2 = new File("2.jpg");

                final int[] count = {0};

                Uploads uploads = new Uploads();
                uploads.uploadImage(f, key, 1, imageDetails, new Runnable() {
                    @Override
                    public void run() {
                        count[0]++;

                    }
                });
                uploads.uploadImage(f2, key, 2, imageDetails, new Runnable() {
                    @Override
                    public void run() {
                        count[0]++;
                    }
                });

                while (count[0] != 2){
                    try {
                        Thread.sleep(300);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }

                f.delete();
                f2.delete();

                if(!cancel[0]){
                    fireDB.save(key, imageDetails, new Runnable() {
                        @Override
                        public void run() {
                            dlg.dispose();
                        }
                    });
                }
                else{
                    dlg.dispose();
                }
            }
        });

        thread.start();
        dlg.setVisible(true);


        if(!cancel[0]){
            File f = new File(fileName1);
            f.delete();

            File f2 = new File(fileName2);
            f2.delete();


            JOptionPane.showMessageDialog(_this, "Item saved");
            refreshImageCount();
            resetEverything();

        }


    }

    public static void refreshImageCount(){
        _this.setTitle("Loading...");
        fireDB.getImagesCount(null);
    }

    public static void updateImageCount(long newCount){
        _this.setTitle("Total records: " + newCount);
    }


    private static void resizeFile(String fileName, int i){
        BufferedImage originalImage = null;
        try {
            originalImage = ImageIO.read(new File(fileName));
            int type = originalImage.getType() == 0? BufferedImage.TYPE_INT_ARGB : originalImage.getType();
            BufferedImage resizeImageJpg = resizeImage(originalImage, type);
            ImageIO.write(resizeImageJpg, "jpg", new File(i + ".jpg"));
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static BufferedImage resizeImage(BufferedImage originalImage, int type) {
        BufferedImage resizedImage = new BufferedImage(450, 300, type);
        Graphics2D g = resizedImage.createGraphics();
        g.drawImage(originalImage, 0, 0, 450, 300, null);
        g.dispose();
        return resizedImage;
    }

    public static  void resetEverything(){
        reset();
        canvases.clear();
        mirrorCanvases.clear();
        pane.removeAll();
        fileName1 = fileName2 = "";
        ArrayList<String> files = getAllFiles();
        if(files.size() >= 2){
            fileName1 = files.get(0);
            fileName2 = files.get(1);
            setImages(fileName1, fileName2);
        }

        pane.repaint();
    }

    public static ArrayList<String> getAllFiles(){
        ArrayList<String> files = new ArrayList<String>();
        File folder = new File(".");
        File[] listOfFiles = folder.listFiles();

        for (int i = 0; i < listOfFiles.length; i++) {
            if (listOfFiles[i].isFile() && isImageExtension(listOfFiles[i])) {
                files.add(listOfFiles[i].getName());
            }
        }

        Collections.sort(files);
        return  files;
    }

    public static boolean isImageExtension(File file){
        String extension = "";

        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        extension = extension.toLowerCase();
        return extension.equals("jpeg") || extension.equals("jpg") || extension.equals("png");
    }

    public static void setImages(String image1, String image2){
        ImageIcon pic = new ImageIcon(image1);
        pic = new ImageIcon(pic.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_DEFAULT));
        JLabel label = new JLabel(pic);
        label.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        pane.add(label);


        ImageIcon pic2 = new ImageIcon(image2);
        pic2 = new ImageIcon(pic2.getImage().getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_DEFAULT));
        JLabel label2 = new JLabel(pic2);
        label2.setSize(IMAGE_WIDTH, IMAGE_HEIGHT);
        label2.setLocation(IMAGE_WIDTH + 10, 0);
        pane.add(label2);

        label.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                mouseIsDragged(mouseEvent.getX(), mouseEvent.getY());
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        label.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                mouseIsReleased();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });


        label2.addMouseMotionListener(new MouseMotionListener() {

            @Override
            public void mouseDragged(MouseEvent mouseEvent) {
                mouseIsDragged(mouseEvent.getX(), mouseEvent.getY());
            }

            @Override
            public void mouseMoved(MouseEvent mouseEvent) {

            }
        });

        label2.addMouseListener(new MouseListener() {
            @Override
            public void mouseClicked(MouseEvent mouseEvent) {

            }

            @Override
            public void mousePressed(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                mouseIsReleased();
            }

            @Override
            public void mouseEntered(MouseEvent mouseEvent) {

            }

            @Override
            public void mouseExited(MouseEvent mouseEvent) {

            }
        });

    }



    public static void mouseIsDragged(int mouseX, int mouseY){
        if(startingX == 0 && startingY == 0){
            startingX = mouseX;
            startingY = mouseY;
        }
        setCanvasPosition(startingX, startingY, mouseX, mouseY);
        setMirrorCanvasPosition(startingX + IMAGE_WIDTH, startingY, mouseX + IMAGE_WIDTH, mouseY);
    }

    public static void popCanvas(){
        if(canvases.size() > 0){
            MyCanvas canvas = canvases.get(canvases.size() - 1);
            pane.remove(canvas);
            canvases.remove(canvas);

            MyCanvas canvas2 = mirrorCanvases.get(mirrorCanvases.size() - 1);
            pane.remove(canvas2);
            mirrorCanvases.remove(canvas2);

            pane.repaint();
        }
    }

    public static void mouseIsReleased(){
        startingX =  startingY = 0;

        if(checkIsValidCanvas()){
            saveCanvas();
        }
        reset();

    }

    public static boolean checkIsValidCanvas(){


        if(canvases.size() >=5) return false;

        if(canvas != null){
            if(canvas.getMyWidth() <= 10 || canvas.getMyHeight() <= 10) return false;

            if(canvas.getFinalX() >= IMAGE_WIDTH || canvas.getFinalY() >= IMAGE_HEIGHT || canvas.getStartX() < 0 || canvas.getStartY() < 0){
                return false;
            }

            for(MyCanvas myCanvas : canvases){

               if(myCanvas.intersect(canvas)){
                   return  false;
               }

            }
        }

        return true;
    }

    public static void saveCanvas(){
        if(canvas != null){
            MyCanvas myCanvas = new MyCanvas(canvas.getStartX(), canvas.getStartY(), canvas.getFinalX(), canvas.getFinalY());
            myCanvas.setSize(2000, 2000);
            pane.add(myCanvas, 0);
            pane.repaint();
            canvases.add(myCanvas);
        }

        if(mirrorCanvas != null){
            MyCanvas myCanvas = new MyCanvas(mirrorCanvas.getStartX(), mirrorCanvas.getStartY(), mirrorCanvas.getFinalX(), mirrorCanvas.getFinalY());
            myCanvas.setSize(2000, 2000);
            pane.add(myCanvas, 0);
            pane.repaint();
            mirrorCanvases.add(myCanvas);
        }

    }

    public static void reset(){
        if(canvas != null) {
            pane.remove(canvas);
            pane.repaint();
            canvas = null;
        }

        if(mirrorCanvas != null){
            pane.remove(mirrorCanvas);
            pane.repaint();
            mirrorCanvas = null;
        }
    }


    public static void setCanvasPosition(int startX, int startY, int finalX, int finalY){
        if(canvas != null) pane.remove(canvas);

        canvas = new MyCanvas(startX, startY, finalX, finalY);
        canvas.setSize(2000, 2000);
        pane.add(canvas, 0);
        pane.repaint();

    }

    public static void setMirrorCanvasPosition(int startX, int startY, int finalX, int finalY){
        if(mirrorCanvas != null) pane.remove(mirrorCanvas);

        mirrorCanvas = new MyCanvas(startX + 10, startY, finalX + 10, finalY);
        mirrorCanvas.setSize(2000, 2000);
        pane.add(mirrorCanvas, 0);
        pane.repaint();

    }
}




class MyCanvas extends JComponent {

    private int startX, startY, finalX, finalY;

    public MyCanvas(int _x, int _y, int _finalX, int _finalY) {
      setPosition(_x, _y, _finalX, _finalY);
    }

    public void setPosition(int _x, int _y, int _finalX, int _finalY){
        if(_finalX > _x){
            startX = _x;
            finalX = _finalX;
        }
        else{
            startX = _finalX;
            finalX = _x;
        }

        if(_finalY > _y){
            startY = _y;
            finalY = _finalY;
        }
        else{
            startY = _finalY;
            finalY = _y;
        }

    }

    public int getMyWidth(){
        return finalX - startX;
    }

    public int getMyHeight(){
        return finalY - startY;
    }

    public boolean intersect(MyCanvas anotherCanvas){
        return this.getStartX() < anotherCanvas.getStartX() + anotherCanvas.getMyWidth()
                && this.getStartX()  + this.getMyWidth() > anotherCanvas.getStartX() &&
                this.getStartY() < anotherCanvas.getStartY() + anotherCanvas.getMyHeight() &&
                this.getStartY() + this.getMyHeight() > anotherCanvas.getStartY();
    }

    public int getStartX() {
        return startX;
    }

    public int getStartY() {
        return startY;
    }

    public int getFinalX() {
        return finalX;
    }

    public int getFinalY() {
        return finalY;
    }

    public void paint(Graphics g) {
        g.setColor(Color.RED);
        g.drawRect(startX, startY, finalX - startX, finalY- startY);
    }

    public String toString(){
        return startX + "," + startY + "-" + finalX + "," + finalY;
    }

}




