package unl.edu.cc.workunity.faces;

/**
 * @author wduck (Wilman Chamba Z)
 */

import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.FacesContext;
import unl.edu.cc.workunity.view.security.UserPrincipalDTO;

public class FacesUtil {

    public static UserPrincipalDTO getCurrentUser() {
        return (UserPrincipalDTO) FacesContext.getCurrentInstance()
                .getExternalContext()
                .getUserPrincipal();
    }

    public static void addSuccessMessage(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    public static void addSuccessMessageAndKeep(String summary, String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_INFO, summary, detail);
    }

    public static void addSuccessMessage(String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, null, detail);
    }

    public static void addSuccessMessageAndKeep(String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_INFO, null, detail);
    }

    public static void addErrorMessage(String summary, String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    public static void addErrorMessageAndKeep(String summary, String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_ERROR, summary, detail);
    }

    public static void addErrorMessage(String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, null, detail);
    }

    public static void addErrorMessageAndKeep(String detail) {
        addMessageAndKeep(FacesMessage.SEVERITY_ERROR, null, detail);
    }

    /**
     * Add messages in the same view
     * @param severity
     * @param summary
     * @param detail
     */
    public static void addMessage(FacesMessage.Severity severity,  String summary, String detail){
        FacesMessage fc = new FacesMessage(severity, summary, detail);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, fc);
    }

    /**
     * Add messages between navigation views
     * @param severity
     * @param summary
     * @param detail
     */
    public static void addMessageAndKeep(FacesMessage.Severity severity,  String summary, String detail){
        FacesMessage fc = new FacesMessage(severity, summary, detail);
        FacesContext facesContext = FacesContext.getCurrentInstance();
        facesContext.addMessage(null, fc);
        facesContext.getExternalContext().getFlash().setKeepMessages(true);
    }
}

