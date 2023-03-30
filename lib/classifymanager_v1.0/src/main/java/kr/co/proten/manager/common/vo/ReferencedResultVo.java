package kr.co.proten.manager.common.vo;

import java.util.List;
import java.util.Map;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
@Data
public class ReferencedResultVo {
	private List<String> noReferencedIdList;
	private Map<String, List<ClassifyRuleReferecedInfoVo>> classifyRuleReferencedIdInfoMap;
}
