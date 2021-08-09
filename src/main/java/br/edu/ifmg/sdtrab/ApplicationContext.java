package br.edu.ifmg.sdtrab;

import br.edu.ifmg.sdtrab.controller.ControlController;
import br.edu.ifmg.sdtrab.controller.DirectoryService;
import br.edu.ifmg.sdtrab.controller.NodeController;
import br.edu.ifmg.sdtrab.controller.StorageController;

public class ApplicationContext {

    private NodeController nodeController;

    public ApplicationContext(boolean worker) throws Exception {
        nodeController = new NodeController(worker);
    }

}
