package src.repository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import src.VO.Tool;
import src.exception.InvalidArgException;
import src.utils.Constants;

public class ToolRepository {
    // Only one tool repository is allowed at one time.
    private static ToolRepository instance = null;
    public static ToolRepository GetInstance() {
        if (instance == null) {
            instance = new ToolRepository();
        }
        return instance;
    }
    
    public ToolRepository() {
        toolLibrary = new HashMap<>();
        for (int toolIndex = 0; toolIndex < Constants.toolBrands.size(); toolIndex++) {
            Tool newTool = new Tool(
                Constants.toolCodes.get(toolIndex),
                Constants.toolTypes.get(toolIndex),
                Constants.toolBrands.get(toolIndex)
            );
            toolLibrary.put(newTool.getToolCode(), newTool);
        }
    }
    
    private Map<String, Tool> toolLibrary;

    public int GetNumberOfTools() {
        return toolLibrary.size();
    }
    
    public Tool GetToolByCode(String toolCode) throws InvalidArgException {
        Tool tool = null;

        try {
            tool = toolLibrary.get(toolCode);
        } catch(NullPointerException e) {
            String errorMessage = String.format(Constants.exceptionMessageInvalidArg, Constants.fieldToolCode, toolCode);
            throw new InvalidArgException(e.getMessage(), Constants.fieldToolCode, errorMessage);
        }

        return tool;
    }

    public List<Tool> GetTools() {
        List<Tool> tools = new ArrayList<>(toolLibrary.values());
        return tools;
    }
}