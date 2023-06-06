package mywiimote;

/**
 * Enumerates the models that the library supports.
 * 
 * @author Pablo Rangel <pablorangel@gmail.com>
 */
enum Model {
    M1 ("Nintendo RVL-CNT-01-TR");
    
    private String modelName;
    
    Model(String modelName){
        this.modelName = modelName;
    }
    
    @Override
    public String toString(){
        return modelName;
    }

    public String getModelName() {
        return modelName;
    }
}
