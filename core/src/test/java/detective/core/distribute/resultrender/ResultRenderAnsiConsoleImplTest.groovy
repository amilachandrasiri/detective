package detective.core.distribute.resultrender;

import java.util.ArrayList;
import java.util.List;

import org.fusesource.jansi.Ansi;
import org.junit.Test;

import detective.core.distribute.JobRunResult;
import junit.framework.TestCase;

/**
 * The class <code>ResultRenderAnsiConsoleImplTest</code> contains tests for
 * the class {@link <code>ResultRenderAnsiConsoleImpl</code>}
 *
 * @pattern JUnit Test Case
 *
 * @generatedBy CodePro at 30/03/15 3:01 PM
 *
 * @author james
 *
 * @version $Revision$
 */
public class ResultRenderAnsiConsoleImplTest extends TestCase {

  /**
   * Construct new test instance
   *
   * @param name the test name
   */
  public ResultRenderAnsiConsoleImplTest(String name) {
    super(name);
  }
  
  /**
   * To Run this main method, you can :
   * mvn exec:java -Dexec.mainClass="detective.core.distribute.resultrender.ResultRenderAnsiConsoleImplTest" -Dexec.classpathScope=test
   * @param args
   */
  public static void main(String[] args) {
    ResultRenderAnsiConsoleImplTest test = new ResultRenderAnsiConsoleImplTest("ResultRenderAnsiConsoleImplTest");
    test.testRender();
  }
  
  @Test
  public void testRender() {
    ResultRenderAnsiConsoleImpl render = new ResultRenderAnsiConsoleImpl();
    
    List<JobRunResult> results = createTestCases();
    
    render.render(results, 0);
  }

  private List<JobRunResult> createTestCases() {
    List<JobRunResult> results = new ArrayList<JobRunResult>();
    
    results.add(createResult("Story1", "Scenario1", true, false, null));
    results.add(createResult("Story2", "Scenario2", false, false, null));
    results.add(createResult("Story3", "Scenario3", false, true, null));
    
    results.add(createResult("Story4", "Scenario4", false, false, createNewException("Ansi Console with Exception Message.")));
    return results;
  }
  
  @Test
  public void testOneResult() {
    ResultRenderAnsiConsoleImpl render = new ResultRenderAnsiConsoleImpl();
    List<JobRunResult> results = createTestCases();
    
    results.each{
      println render.createAnsiCode(it).toString()
    }
    
//    assert """
//[32;1mStory Name: Story1[22m
//[1m| -- Scenario Name: [22mScenario1
//[1m| -- Successed:     [22mYes
//[m
//    """.equals(render.createAnsiCode(results.get(0)).toString())
//    
//    assert """
//[31;1mStory Name: Story2[22m
//[1m| -- Scenario Name: [22mScenario2
//[1m| -- Successed:     [22mFailed
//[m
//    """.equals(render.createAnsiCode(results.get(1)).toString())
//    
//    assert """
//[34;1mStory Name: Story3[22m
//[1m| -- Scenario Name: [22mScenario3
//[1m| -- Ignored:       Yes[22m
//[m
//    """.equals(render.createAnsiCode(results.get(2)).toString())
  }
  
  private Throwable createNewException(String msg){
    try{
      throw new RuntimeException(msg);
    }catch (Exception e){
      return e;
    }
  }
  
  private JobRunResult createResult(String storyName, String scenarioName, boolean successed, boolean ignored, Throwable error){
    JobRunResult result = new JobRunResult();
    result.setStoryName(storyName);
    result.setScenarioName(scenarioName);
    result.setSuccessed(successed);
    result.setIgnored(ignored);
    result.setError(error);
    return result;
  }
  
}

/*$CPS$ This comment was generated by CodePro. Do not edit it.
 * patternId = com.instantiations.assist.eclipse.pattern.testCasePattern
 * strategyId = com.instantiations.assist.eclipse.pattern.testCasePattern.junitTestCase
 * additionalTestNames = 
 * assertTrue = false
 * callTestMethod = true
 * createMain = false
 * createSetUp = false
 * createTearDown = false
 * createTestFixture = false
 * createTestStubs = false
 * methods = 
 * package = detective.core.distribute.resultrender
 * package.sourceFolder = detective.core/src/test/java
 * superclassType = junit.framework.TestCase
 * testCase = ResultRenderAnsiConsoleImplTest
 * testClassType = detective.core.distribute.resultrender.ResultRenderAnsiConsoleImpl
 */