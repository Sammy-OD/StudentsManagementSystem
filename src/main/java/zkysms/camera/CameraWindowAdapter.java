package zkysms.camera;

import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class CameraWindowAdapter extends WindowAdapter {

    public CameraWindowAdapter(){}

    @Override
    public void windowClosing(WindowEvent evt) {
        Camera.getWebcam().close();
        if(Camera.getCameraCaller().contentEquals("zkysms.ui.registrationform.teacher.RegistrationFormController")){
            zkysms.form.teacher.RegistrationForm.getScrollPane().setDisable(false);
        }else if(Camera.getCameraCaller().contentEquals("zkysms.ui.registrationform.student.RegistrationFormController")){
            zkysms.form.student.RegistrationForm.getScrollPane().setDisable(false);
        }
    }

}