package stdmansys.camera;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CameraWindowAdapter extends WindowAdapter {

    public CameraWindowAdapter(){}

    @Override
    public void windowClosing(WindowEvent evt) {
       Camera.getWebcam().close();
       if(Camera.getCameraCaller().contentEquals("stdmansys.registrationform.teacher.RegistrationFormController")){
           stdmansys.registrationform.teacher.RegistrationForm.getRootNode().setDisable(false);
       }
    }

}