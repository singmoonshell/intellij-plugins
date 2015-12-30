package training.solutions.Navigation;

import training.commands.ExecutionList;
import training.lesson.LessonProcessor;
import training.solutions.BaseSolutionClass;
import training.testFramework.LessonSolution;
import training.util.PerformActionUtil;

/**
 * Created by karashevich on 28/12/15.
 */
public class JumpToSourceSolution implements LessonSolution {
    @Override
    public void solveStep() throws Exception {
        final ExecutionList currentExecutionList = LessonProcessor.getCurrentExecutionList();
        if (currentExecutionList == null) return;

        int stepNumber = currentExecutionList.getElements().size() - 1;


        if (stepNumber == 3){
            final String actionName = "EditSource";
            PerformActionUtil.performActionDisabledPresentation(actionName, currentExecutionList.getEditor());
        }
        if (stepNumber == 0){
            final String actionName = "EditSource";
            BaseSolutionClass.gotoOffset(currentExecutionList.getEduEditor(), 263);
            PerformActionUtil.performActionDisabledPresentation(actionName, currentExecutionList.getEditor());
        }
    }
}
