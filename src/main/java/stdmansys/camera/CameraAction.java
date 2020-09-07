package stdmansys.camera;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.time.LocalDate;

public class CameraAction implements ActionListener {

    public CameraAction(){}

    @Override
    public void actionPerformed(ActionEvent evt) {
        if(evt.getSource() == Camera.getTakePhotoBtn()){
            Camera.setCapturedImage(Camera.getWebcam().getImage());
            Camera.getImageView().setIcon(new ImageIcon(Camera.getCapturedImage()));
            Camera.getCameraFrame().setContentPane(Camera.getContentPane2());
            Camera.getCameraFrame().revalidate();
            Camera.getCameraFrame().repaint();
        }

        if(evt.getSource() == Camera.getRetakeBtn()){
            Camera.getCameraFrame().setContentPane(Camera.getContentPane1());
            Camera.getCameraFrame().revalidate();
            Camera.getCameraFrame().repaint();
            Camera.setCapturedImage(null);
        }

        if(evt.getSource() == Camera.getSaveBtn()){
            Camera.setImageName("IMG" + LocalDate.now().toString().replace("-", "") + "_"
                    + (int) (Math.random() * 10000) + "_"
                    + (int) (Math.random() * 10));
            try{
                ImageIO.write(Camera.getCapturedImage(), "PNG", new File("image/" + Camera.getImageName() + ".png"));
                Camera.getWebcam().close();
                Camera.getCameraFrame().dispose();
                if(Camera.getCameraCaller().contentEquals("stdmansys.registrationform.teacher.RegistrationFormController")){
                    stdmansys.registrationform.teacher.RegistrationForm.getRootNode().setDisable(false);
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }

}