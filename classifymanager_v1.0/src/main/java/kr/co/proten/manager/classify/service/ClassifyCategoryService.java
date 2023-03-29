package kr.co.proten.manager.classify.service;

import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;

public interface ClassifyCategoryService {
	
	/**
	 * 카테고리 데이터 계층화
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> convertCategoryListConvHierarchyData(List<Map<String, Object>> categoryList) throws Exception;
		
	/**
	 * 카테고리 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectCategoryList(Map<String, Object> paramMap, boolean withRootData) throws Exception;
	
	/**
	 * 카테고리 상세정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> selectViewCategory(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 하위 카테고리 상세정보 조회
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	List<Map<String, Object>> getCategoryChild(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 카테고리 저장
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	 Map<String,Object> insertCategoryInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	
	/**
	 * 카테고리 수정
	 * @param paramMap
	 * @param session
	 * @return
	 * @throws Exception
	 */
	 Map<String,Object> updateCategoryInfo(Map<String, Object> paramMap, HttpSession session) throws Exception;
	 
	 /**
	 * 카테고리 벌크 저장
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean insertCategoryBulk(List<Map<String, Object>> listMap) throws Exception;
		
	/**
	 * 카테고리 벌크 수정
	 * @param listMap
	 * @return
	 * @throws Exception
	 */
	boolean updateCategoryBulk(List<Map<String, Object>> listMap) throws Exception;	 
	
	/**
	 * 카테고리 삭제
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean deleteCategoryInfo(Map<String, Object> paramMap) throws Exception;
	
	/**
	 * 카테고리 중복 체크
	 * @param paramMap
	 * @return
	 * @throws Exception
	 */
	boolean dupCategoryInfo(Map<String, Object> paramMap) throws Exception;	
	
	/**
	 * 아이디 발급
	 * @return
	 * @throws Exception
	 */
	int selectMaxId() throws Exception; 
}
