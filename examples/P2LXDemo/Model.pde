/**
 * This is a very basic model class that is a 3-D matrix
 * of points. The model contains just one fixture.
 */
static class Model extends LXModel {
  
  public Model() {
    super(new Fixture());
  }
  
  private static class Fixture extends LXAbstractFixture {
    
    private static final int MATRIX_SIZE = 12;
    
    private Fixture() {
      // Here's the core loop where we generate the positions
      // of the points in our model
      for (int x = 0; x < MATRIX_SIZE; ++x) {
        for (int y = 0; y < MATRIX_SIZE; ++y) {
          for (int z = 0; z < MATRIX_SIZE; ++z) {
            // Add point to the fixture
            addPoint(new LXPoint(x*FEET, y*FEET, z*FEET));
          }
        }
      }
    }
  }
}

