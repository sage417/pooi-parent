package app.pooi.workflow.domain.model.workflow.diagram;

import java.util.List;

public record ProcessDiagramElement(String id, String name, List<ProcessDiagramElement> outgoings) {


}
