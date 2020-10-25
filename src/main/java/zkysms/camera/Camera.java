package zkysms.camera;

import com.github.sarxos.webcam.Webcam;
import com.github.sarxos.webcam.WebcamPanel;
import com.github.sarxos.webcam.WebcamResolution;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;

public class Camera {

    private static JFrame cameraFrame;
    private static Webcam webcam;
    private static JButton takePhotoBtn, saveBtn, retakeBtn;
    private static String cameraCaller, imageName;
    private static JLabel imageView;
    private static JPanel contentPane1, contentPane2;
    private static BufferedImage capturedImage;

    public static void initWebcam(){
        if(Webcam.getDefault() != null){
            webcam = Webcam.getDefault();
            webcam.setViewSize(WebcamResolution.VGA.getSize());
        }
    }

    private static void initCamera() {
        if(Camera.getCameraCaller().contentEquals("zkysms.ui.registrationform.teacher.RegistrationFormController")){
            zkysms.form.teacher.RegistrationForm.getScrollPane().setDisable(true);
        }else if(Camera.getCameraCaller().contentEquals("zkysms.ui.registrationform.student.RegistrationFormController")){
            zkysms.form.student.RegistrationForm.getScrollPane().setDisable(true);
        }

        WebcamPanel webcamPanel = new WebcamPanel(webcam);
        GridBagConstraints webcamPanelConstraint = new GridBagConstraints();
        webcamPanelConstraint.gridy = 0;
        webcamPanelConstraint.insets = new Insets(0, 40, 0, 40);

        imageView = new JLabel();
        GridBagConstraints imageViewConstraint = new GridBagConstraints();
        imageViewConstraint.gridy = 0;
        imageViewConstraint.insets = new Insets(0, 40, 0, 40);

        takePhotoBtn = new JButton("Take Photo");
        takePhotoBtn.addActionListener(new CameraAction());

        saveBtn = new JButton("Save");
        saveBtn.addActionListener(new CameraAction());

        retakeBtn = new JButton("Retake");
        retakeBtn.addActionListener(new CameraAction());

        JPanel btnPanel1 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel1.setBackground(Color.BLACK);
        GridBagConstraints btnPanel1Constraint = new GridBagConstraints();
        btnPanel1Constraint.gridy = 1;
        btnPanel1.add(takePhotoBtn);

        JPanel btnPanel2 = new JPanel(new FlowLayout(FlowLayout.CENTER));
        btnPanel2.setBackground(Color.BLACK);
        GridBagConstraints btnPanel2Constraint = new GridBagConstraints();
        btnPanel2Constraint.gridy = 1;
        btnPanel2.add(saveBtn);
        btnPanel2.add(retakeBtn);

        contentPane1 = new JPanel(new GridBagLayout());
        contentPane1.setBackground(Color.BLACK);
        contentPane1.add(webcamPanel, webcamPanelConstraint);
        contentPane1.add(btnPanel1, btnPanel1Constraint);

        contentPane2 = new JPanel(new GridBagLayout());
        contentPane2.setBackground(Color.BLACK);
        contentPane2.add(imageView, imageViewConstraint);
        contentPane2.add(btnPanel2, btnPanel2Constraint);

        cameraFrame = new JFrame("Camera");
        cameraFrame.setContentPane(contentPane1);
        cameraFrame.addWindowListener(new CameraWindowAdapter());
        cameraFrame.pack();
        cameraFrame.setLocationRelativeTo(null);
        cameraFrame.setVisible(true);
    }

    public static void launch() {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                if(webcam != null){
                    initCamera();
                }
            }
        });
    }

    public static JFrame getCameraFrame(){
        return cameraFrame;
    }

    public static Webcam getWebcam() {
        return webcam;
    }

    public static JButton getTakePhotoBtn(){
        return takePhotoBtn;
    }

    public static JButton getSaveBtn(){
        return saveBtn;
    }

    public static JButton getRetakeBtn(){
        return retakeBtn;
    }

    public static String getCameraCaller(){
        return cameraCaller;
    }

    public static void setCameraCaller(String cameraCaller){
        Camera.cameraCaller = cameraCaller;
    }

    public static String getImageName(){
        return imageName;
    }

    public static JPanel getContentPane1() {
        return contentPane1;
    }

    public static JPanel getContentPane2() {
        return contentPane2;
    }

    public static JLabel getImageView() {
        return imageView;
    }

    public static BufferedImage getCapturedImage(){
        return capturedImage;
    }

    public static void setImageName(String imageName){
        Camera.imageName = imageName;
    }

    public static void setCapturedImage(BufferedImage capturedImage){
        Camera.capturedImage = capturedImage;
    }

}