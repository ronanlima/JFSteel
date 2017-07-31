package br.com.home.maildeliveryjfsteel.async;

import java.util.List;

import br.com.home.maildeliveryjfsteel.firebase.FirebaseService;

/**
 * Created by Ronan.lima on 29/07/17.
 */

public class FirebaseAsyncParam {
    private List genericDTO;
    private FirebaseService fService;

    public FirebaseAsyncParam(List genericDTO, FirebaseService fService) {
        this.genericDTO = genericDTO;
        this.fService = fService;
    }

    public List getGenericDTO() {
        return genericDTO;
    }

    public void setGenericDTO(List genericDTO) {
        this.genericDTO = genericDTO;
    }

    public FirebaseService getfService() {
        return fService;
    }

    public void setfService(FirebaseService fService) {
        this.fService = fService;
    }
}
