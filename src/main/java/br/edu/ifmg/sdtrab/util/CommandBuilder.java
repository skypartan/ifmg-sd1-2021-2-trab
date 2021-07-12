package br.edu.ifmg.sdtrab.util;

import br.edu.ifmg.sdtrab.ApplicationContext;
import br.edu.ifmg.sdtrab.ui.WindowCommand;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;

public class CommandBuilder {

    private Class<? extends WindowCommand> newCommand;

    private final ApplicationContext context;
    private HashMap<String, WindowCommand> registry;


    public CommandBuilder(ApplicationContext context) {
        this.context = context;
    }

    //<editor-fold desc="Build methods">

    public CommandBuilder type(Class<? extends WindowCommand> type) {
        newCommand = type;
        return this;
    }

    public CommandBuilder registry(HashMap<String, WindowCommand> registry) {
        this.registry = registry;
        if (registry == null)
            throw new NullPointerException();

        return this;
    }

    public WindowCommand build() throws NoSuchMethodException, InvocationTargetException, InstantiationException,
            IllegalAccessException {

        var command = newCommand.getConstructor().newInstance();
        registry.put(command.getName(), command);

        injectFields(command);
        construct(command);

        return command;
    }

    //</editor-fold>

    //<editor-fold desc="Dependency injection">

    public void injectFields(WindowCommand command) throws IllegalAccessException {
        for (var field : command.getClass().getDeclaredFields()) {
            var injectFieldAnnotation = field.getAnnotationsByType(InjectField.class);
            if (injectFieldAnnotation.length != 0) {
                if (field.getType().equals(ApplicationContext.class))
                    field.set(command, context);
                if (field.getType().equals(HashMap.class))
                    field.set(command, registry);
            }
        }
    }

    public void construct(WindowCommand command) throws InvocationTargetException, IllegalAccessException {
        for (var method : command.getClass().getMethods()) {
            var postInjectAnnotation = method.getAnnotationsByType(PostInject.class);
            if (postInjectAnnotation.length != 0)
                method.invoke(command);
        }
    }

    //</editor-fold>
}
