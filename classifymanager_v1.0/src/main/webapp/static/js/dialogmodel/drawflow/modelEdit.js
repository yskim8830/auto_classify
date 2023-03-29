let CTX_PATH = '';
//ROOT 컨텍스트 변수
let rootCtxVarNameSet;
//답변이미지 참조 정보
let refAnserImgMap;
//대화 정보
let refDialogMap;
//시나리오 정보
let refDialogModelMap;
//url link 정보
let refUrlLinkMap;
//app link 정보
let refAppLinkMap;
//api 정보
let refApiMap;

let gatewayBtnIdx = '0';
let conditionBtnIfIdx = '1';
let conditionBtnElseIdx = '2';

let slotBtnSuccessIdx = '1';
let slotBtnFailIdx = '2';

let apiBtnExistsIdx = '1';
let apiBtnNotExistsIdx = '2';
let apiBtnExceptionIdx = '3';
let apiBtnExistsName = 'exists';
let apiBtnNotExistsName = 'notexists';
let apiBtnExceptionName = 'exception';

function isObjEmpty(param) {
	return Object.keys(param).length === 0;
}

function loadModelData(data){
	var dataObj = JSON.parse(data);
	
	var convertedJson = '';
	var convertedRootObj = new Object();
	var convertedDrawFlowObj = new Object();
	var convertedHomeObj = new Object();
	var convertedDataObj = new Object();
	convertedRootObj.drawflow = convertedDrawFlowObj;
	convertedDrawFlowObj.Home = convertedHomeObj;
	convertedHomeObj.data = convertedDataObj;

	var drawflowNodeObj;
	var drawflowNodeDataObj;
	var drawflowNodeDataNameObj;
	var drawflowNodeDataDetailObj;
	var drawflowNodeDataSortObj;
	var drawflowNodeInputObj;
	var drawflowNodeOutputObj;
	var drawflowNodeConnectionObj;
	dataObj.nodeArr.forEach((el, t) => {
		drawflowNodeObj = new Object();
		drawflowNodeObj.id = el.nodeId;
		drawflowNodeObj.name = el.nodeType;
		drawflowNodeObj.class = el.nodeType;
		drawflowNodeObj.typenode = false;
		drawflowNodeObj.pos_x = el.pos_x;
		drawflowNodeObj.pos_y = el.pos_y;

		drawflowNodeDataObj = new Object();
		drawflowNodeDataNameObj = new Object();
		drawflowNodeDataDetailObj = new Object();
		drawflowNodeDataSortObj = new Object();
		// set data 
		if(!isEmpty(el.data.message)){
			drawflowNodeDataObj.template = el.data.message;
		}
		if(!isEmpty(el.data.image)){
			drawflowNodeDataObj.image = el.data.image;
		}
		var idx;
		var buttonIsEmpty = true;
		
		if(el.nodeType == 'metaNode'){
			drawflowNodeDataObj.meta = new Object();
			drawflowNodeDataObj.meta.type = el.data.infoObj.type;
			drawflowNodeDataObj.meta.value = el.data.infoObj.value;
		}
		
		if(el.nodeType == 'apiNode'){
			drawflowNodeDataObj.api = el.data.infoObj.api;
			drawflowNodeDataObj.param = el.data.infoObj.param;
		}
		
		if(el.nodeType == 'slotNode'){
			drawflowNodeDataObj.slot = el.data.infoObj.slot;
		}
			
		el.data.attachedArr.forEach((a, t) => {
			console.log('data.attachedArr : ' + el.nodeType);
			idx = a.id.slice(7);
			if(a.type == 'button'){
				buttonIsEmpty = false;
				drawflowNodeDataNameObj[idx] = a.label;
				drawflowNodeDataDetailObj[idx] = a.data;
				drawflowNodeDataSortObj[idx] = a.sort;
			}
			
			if(a.type == 'condition'){
				buttonIsEmpty = false;
				drawflowNodeDataNameObj[idx] = a.label;
				drawflowNodeDataDetailObj[idx] = a.data;
				drawflowNodeDataSortObj[idx] = a.sort;
			}
			
			if(el.nodeType == 'apiNode'){
				buttonIsEmpty = false;
				drawflowNodeDataNameObj[idx] = a.label;
				drawflowNodeDataDetailObj[idx] = a.data;
				drawflowNodeDataSortObj[idx] = a.sort;
			}
		});
		if(!buttonIsEmpty){
			drawflowNodeDataObj.name = drawflowNodeDataNameObj;
			drawflowNodeDataObj.detail = drawflowNodeDataDetailObj;
			drawflowNodeDataObj.sort = drawflowNodeDataSortObj;
		}
		drawflowNodeObj.data = drawflowNodeDataObj;

		// set inputs 
		drawflowNodeInputObj = new Object();
		drawflowNodeObj.inputs = drawflowNodeInputObj;
		if(!isEmpty(el.preNodeArr)){
			drawflowNodeInputObj.input_1 = new Object();
			drawflowNodeInputObj.input_1.connections = new Array();
			el.preNodeArr.forEach((a, t) => {
				drawflowNodeConnectionObj = new Object(); 
				drawflowNodeConnectionObj.node = a.nodeId;
				drawflowNodeConnectionObj.input = a.attachedId;
				drawflowNodeInputObj.input_1.connections.push(drawflowNodeConnectionObj);
			});
		} else {
			if(drawflowNodeObj.name != 'startNode'){
				drawflowNodeInputObj.input_1 = new Object();
				drawflowNodeInputObj.input_1.connections = new Array();
			}
		}

		// set outputs
		drawflowNodeOutputObj = new Object();
		drawflowNodeObj.outputs = drawflowNodeOutputObj;
		if(el.nodeType != 'metaNode'){
			el.data.attachedArr.forEach((a, t) => {
					idx = a.id.slice(7);
					drawflowNodeConnectionObj = new Object(); 
					drawflowNodeConnectionObj.node = a.nextNodeId;
					drawflowNodeConnectionObj.output = 'input_1';
					
					drawflowNodeOutputObj[a.id] = new Object();
					drawflowNodeOutputObj[a.id].connections = new Array();
					if(a.nextNodeId != null){
						drawflowNodeOutputObj[a.id].connections.push(drawflowNodeConnectionObj);
					}
			});
			
			if(drawflowNodeObj.name == 'startNode' && isEmpty(el.data.attachedArr)){
				drawflowNodeOutputObj.output_1 = new Object();
				drawflowNodeOutputObj.output_1.connections = new Array();
			}
		}
		drawflowNodeObj.html = getNodeHtml(el);
		convertedDataObj[el.nodeId] = drawflowNodeObj;
	});
	
	editor.import(convertedRootObj);
	editor.zoom_refresh();
	$('.parent-node .drawflow-node.basicNode .outputs .output').remove();
	$('.parent-node .drawflow-node.gatewayNode .outputs .output').remove();
	$('.parent-node .drawflow-node.apiNode .outputs .output').remove();
	$('.parent-node .drawflow-node.slotNode .outputs .output').remove();
	$('.parent-node .drawflow-node.metaNode .outputs .output').remove();
	
}

function getNodeHtml(obj) {
	console.log('[getNodeHtml] obj.nodeType : ' + obj.nodeType);
	var nodeHtml = '';
	switch (obj.nodeType) {
		case 'startNode':
			var startNode = `
				<div>
				  <div class="title-box"><i class="fas fa-home" ></i> <span style="font-size:1.05rem;">시작노드</span></div>
				  <div class="box">
				  </div>
				</div>
				`;
			nodeHtml = startNode;
			break;
		case 'metaNode':			
			var metaId = 'meta_' + obj.nodeId;
			var metaTypeId = 'metatype_' + obj.nodeId;
			var metaNameId = 'metaname_' + obj.nodeId;
			var metaContId = 'metacont_' + obj.nodeId; 
			
			var metaTypeValue = obj.data.infoObj.type;
			var metaIdValue = obj.data.infoObj.value;
			var metaNameValue = '';
			var metaContValue = '';
			var metaLinkObj;
			
			var metaNameHtml = '';
			metaNameHtml += '<span class="badge ms-auto bg-secondary" style="top:2px;">';
			if(metaTypeValue == 'urlLink'){
				metaLinkObj = refUrlLinkMap.get(metaIdValue);
				metaNameValue = metaLinkObj.linkNm;
				metaContValue = metaLinkObj.linkValue;
				metaNameHtml += 'URL Link';
			} else if(metaTypeValue == 'appLink'){
				metaLinkObj = refAppLinkMap.get(metaIdValue);
				metaNameValue = metaLinkObj.linkNm;
				metaContValue = metaLinkObj.linkValue;
				metaNameHtml += 'APP Link';
			} else if(metaTypeValue == 'intent'){
				metaNameValue = refDialogMap.get(metaIdValue);
				metaNameHtml += '대화';
			} else if(metaTypeValue == 'scenario'){
				metaNameValue = refDialogModelMap.get(metaIdValue);
				metaNameHtml += '시나리오';
			} else if(metaTypeValue == 'phoneLink'){				
				metaNameValue = '커스텀 데이터';
				metaContValue = metaIdValue;
				metaNameHtml += '전화연결';
			} else if(metaTypeValue == 'userDefine'){				
				metaNameValue = '커스텀 데이터';
				metaContValue = metaIdValue;
				metaNameHtml += '사용자정의';
			} 
			metaNameHtml += '</span>';
			metaNameHtml = metaNameValue + metaNameHtml;
			
			var metaAreaHtml = `
				<div class="col-md-12" style="margin-bottom:10px;">
					<button type="button" id="${metaNameId}" class="btn d-flex btn-light-secondary w-100 d-block text-secondary font-weight-medium">
					  ${metaNameHtml}
					</button>
				</div>
				<textarea df-template id="${metaContId}" style="height:60px;" disabled>${metaContValue}</textarea>
				<input type="hidden" id="${metaId}" value="${metaIdValue}"/>
				<input type="hidden" id="${metaTypeId}" value="${metaTypeValue}"/>
				<div class="control-box" style="display:none;">
					<div class="row" style="margin-top:5px;">
						<div class="col-8">
							<label style="margin-top:5px;"><h6>&nbsp;메타 정보명</h6></label>
						</div>
						<div class="col-4">
							<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
								<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'metaNode', 'meta');">편집</button>
							</div>
						</div>
					</div>
				</div>
				`;
			
			var metaNode = `
				<div>            
					<div class="title-box"><i class="fas fa-code" ></i> <span style="font-size:1.05rem;">메타정보노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:56px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:56px;cursor:pointer;display:none;"></i></div>
					<div class="box">
						${metaAreaHtml}
					</div>					
				</div>
			`;

			nodeHtml = metaNode;
			break;
		case 'apiNode':
			var gatewayAreaHtml = '';
			var buttonPreAreaHtml = '';
			var buttonPostAreaHtml = '';
			var buttonExceptionAreaHtml = '';
			var btnOutIdx = '';
			var btnId = '';
			var btnClass = '';
			var gatewayChecked = '';
			var nextNodeId = '';
			
			var apiNo = obj.data.infoObj.api;
			var apiName = refApiMap.get(apiNo);

			obj.data.attachedArr.forEach((a, t) => {
				idx = a.id.slice(7);
				nextNodeId = a.nextNodeId;
				if(idx == gatewayBtnIdx){
					btnOutIdx = gatewayBtnIdx;
					btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
					btnClass = 'button-' + btnOutIdx;
					var buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center">
								<div class="col-md-12 col-lg-12">
									<button type="button" class="btn waves-effect waves-light btn-secondary" disabled>게이트웨이 연결</button>
								</div>
							</div>
							<div class="output ${a.id}"></div>
						</div>
					`;
					gatewayAreaHtml = buttonHtml;
					gatewayChecked = 'checked';
				}
				if(idx == apiBtnExistsIdx){
					btnOutIdx = apiBtnExistsIdx;
					btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
					btnClass = 'button-' + btnOutIdx;
					var buttonHtml = '';
					if(nextNodeId == null || nextNodeId == ''){
						buttonHtml = `
							<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
								<div class="row justify-content-center" style="padding-left:18px;">
									<div>
										<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="결과가 있을 경우" data-btnId="${btnId}" disabled>결과가 있을 경우</button>
									</div>
								</div>
							</div>
						`;
					} else {
						buttonHtml = `
							<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
								<div class="row justify-content-center" style="padding-left:18px;">
									<div>
										<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="결과가 있을 경우" data-btnId="${btnId}" disabled>결과가 있을 경우</button>
									</div>
								</div>
								<div class="output ${a.id}" style="right: -18px;"></div>
							</div>
						`;
					}
					
					buttonPreAreaHtml = buttonHtml;
				}
				if(idx == apiBtnNotExistsIdx){
					btnOutIdx = apiBtnNotExistsIdx;
					btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
					btnClass = 'button-' + btnOutIdx;
					var buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="결과가 없을 경우" data-btnId="${btnId}" disabled>결과가 없을 경우</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right: -18px;"></div>
						</div>
					`;
					buttonPostAreaHtml = buttonHtml;
				}
				if(idx == apiBtnExceptionIdx){
					btnOutIdx = apiBtnExceptionIdx;
					btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
					btnClass = 'button-' + btnOutIdx;
					var buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="예외발생 경우" data-btnId="${btnId}" disabled>예외발생 경우</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right: -18px;"></div>
						</div>
					`;
					buttonExceptionAreaHtml = buttonHtml;
				}
			});
			
			var apiId = 'api_' + obj.nodeId;
			var apiNameId = 'apiname_' + obj.nodeId;
			var apiNode = `
				<div>            
					<div class="title-box"><i class="fas fa-cogs" ></i> <span style="font-size:1.05rem;">API노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:90px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:90px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
						<div class="col-md-12" style="margin-bottom:10px;">
							<button type="button" id="${apiNameId}" class="btn d-flex btn-light-secondary w-100 d-block text-secondary font-weight-medium">${apiName}</button>
						</div>
						<input type="hidden" id="${apiId}" value="${apiNo}"/>
						
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;display:none;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 버튼</h6></label>
								</div>
								<div class="col-4">
									<div class="form-check form-switch">
										<input class="form-check-input" type="checkbox" style="width:45px;" ${gatewayChecked}/>
									</div>
								</div>
							</div>
							
							<div class="row" style="margin-top:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;1. API 선택</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'apiNode', 'apiReq');">편집</button>
									</div>
								</div>
							</div>
							<div class="row" style="margin-top:5px;margin-bottom:10px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;2. API 응답 조건</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'apiNode', 'apiRes');">편집</button>
									</div>
								</div>
							</div>
							
						</div>
						<div id="btnDiv_pre_${obj.nodeId}" class="apiResCondition">
						${buttonPreAreaHtml}
						</div>
						<div id="btnDiv_${obj.nodeId}" class="apiResCondition">
						</div>
						<div id="btnDiv_post_${obj.nodeId}" class="apiResCondition">
						${buttonPostAreaHtml}
						</div>
						<div id="btnDiv_exception_${obj.nodeId}" class="apiResCondition">
						${buttonExceptionAreaHtml}
						</div>
						<div id="gatewayDiv_${obj.nodeId}" style="padding-top:7px;padding-left:40px;">
						${gatewayAreaHtml}
						</div>
					</div>
					
				</div>
			`;
			nodeHtml = apiNode;
			break;
		case 'gatewayNode':
			var buttonPreAreaHtml = '';
			var buttonAreaHtml = '';
			var buttonPostAreaHtml = '';
			var buttonHtml = '';
			var btnId = '';
			var btnClass = '';
			var btnOutIdx = 1;
			
			obj.data.attachedArr.forEach((a, t) => {
				idx = a.id.slice(7);
				btnOutIdx = idx;
				btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
				btnClass = 'button-' + btnOutIdx;
				
				if(idx == conditionBtnIfIdx){
					buttonPreAreaHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="IF 조건" data-btnId="${btnId}" disabled>IF 조건</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right:-18px;"></div>
						</div>
						`;
				} else if(idx == conditionBtnElseIdx){
					buttonPostAreaHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="ELSE 조건" data-btnId="${btnId}" disabled>ELSE 조건</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right:-18px;"></div>
						</div>
						`;
				} else {
					buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<i class="fas fa-sort fa-lg" style="cursor:pointer;"></i>
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="ELSE IF 조건" data-btnId="${btnId}" disabled>ELSE IF 조건</button>
								</div>
							</div>
							<i class="fas fa-minus" onclick="deleteCondition(event,'${btnId}','${btnOutIdx}');" style="left:187px;cursor:pointer;"></i>
							<div class="output ${a.id}" style="right:-18px;"></div>
						</div>
						`;
					buttonAreaHtml += buttonHtml;
				}
			});

			var gatewayNode = `
				<div>            
					<div class="title-box"><i class="fas fa-code-branch" ></i> <span style="font-size:1.05rem;">응답게이트웨이노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:12px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:12px;cursor:pointer;display:none;"></i></div>
					<div class="box">
						
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 조건</h6></label>
								</div>
								<div class="col-4">
									<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editConditaionBtn(event);">편집</button>
								</div>
							</div>
						</div>
						<div id="btnDiv_pre_${obj.nodeId}">
						${buttonPreAreaHtml}
						</div>
						<div id="btnDiv_${obj.nodeId}">
						${buttonAreaHtml}
						</div>
						<div id="btnDiv_post_${obj.nodeId}">
						${buttonPostAreaHtml}
						</div>
					</div>
				</div>
			`;
			nodeHtml = gatewayNode;
			break;
		case 'slotNode':
			var buttonSuccessAreaHtml = '';
			var buttonFailAreaHtml = '';
			var btnId = '';
			var btnClass = '';
			var btnOutIdx = 1;
			
			obj.data.attachedArr.forEach((a, t) => {
				idx = a.id.slice(7);
				btnOutIdx = idx;
				btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
				btnClass = 'button-' + btnOutIdx;
				
				if(idx == slotBtnSuccessIdx){
					buttonSuccessAreaHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="SUCCESS" data-btnId="${btnId}" disabled>슬롯필링 성공할 경우</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right:-18px;"></div>
						</div>
						`;
				} else if(idx == slotBtnFailIdx){
					buttonFailAreaHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
							<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
								<div>
									<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="FAIL" data-btnId="${btnId}" disabled>슬롯필링 실패할 경우</button>
								</div>
							</div>
							<div class="output ${a.id}" style="right:-18px;"></div>
						</div>
						`;
				}
			});

			var slotNode = `
				<div>            
					<div class="title-box"><i class="fas fa-sync" ></i> <span style="font-size:1.05rem;">SLOT노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:78px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:78px;cursor:pointer;display:none;"></i></div>
					<div class="box">
						
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;슬롯 필링</h6></label>
								</div>
								<div class="col-4">
									<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editSlotBtn(event);">편집</button>
								</div>
							</div>
						</div>
						<div id="btnDiv_success_${obj.nodeId}">
						${buttonSuccessAreaHtml}
						</div>
						<div id="btnDiv_fail_${obj.nodeId}">
						${buttonFailAreaHtml}
						</div>
					</div>
				</div>
			`;
			nodeHtml = slotNode;
			break;
		case 'basicNode':
			var buttonAreaHtml = '';
			var gatewayAreaHtml = '';
			var buttonHtml = '';
			var btnId = '';
			var btnClass = '';
			var btnOutIdx = '1';
			var gatewayChecked = '';
			var imageData = '';		
			var imageDisplay = 'display:none;';
			if(!isEmpty(obj.data.image)){
				imageData = refAnserImgMap.get(obj.data.image);
				imageDisplay = 'display:block;';
			}
			
			obj.data.attachedArr.sort(function (a, b) { 
				return a.sort < b.sort ? -1 : a.sort > b.sort ? 1 : 0;  
			});
			
			obj.data.attachedArr.forEach((a, t) => {
				idx = a.id.slice(7);
				btnOutIdx = idx;
				btnId = 'btn_' + obj.nodeId + '_' + btnOutIdx;
				btnClass = 'button-' + btnOutIdx;
				if(idx == gatewayBtnIdx){
					var buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items:center;">
							<div class="row justify-content-center">
								<div class="col-md-12 col-lg-12">
									<button type="button" class="btn waves-effect waves-light btn-secondary" disabled>게이트웨이 연결</button>
								</div>
							</div>
							<div class="output ${a.id}"></div>
						</div>
					`;
					gatewayAreaHtml = buttonHtml;
					gatewayChecked = 'checked';
				} else {
					buttonHtml = `
						<div class="${btnClass}" id="${btnId}" style="display:flex;align-items:center;padding-top:5px;">
							<i class="fas fa-sort fa-lg" style="cursor:pointer;"></i><input type="text" df-name-${btnOutIdx} placeholder="버튼라벨" value=""/><i class="fas fa-minus" onclick="deleteBtn(event,'${btnId}','${btnOutIdx}');" style="left:187px;cursor:pointer;"></i>
							<div class="output ${a.id}"></div>
						</div>
					`;
					buttonAreaHtml += buttonHtml;
				}
			});
			var basicNode = `
				<div>            
					<div class="title-box"><i class="fas fa-comment" ></i> <span style="font-size:1.05rem;">기본답변노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:58px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:58px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
						<div class="image-box" style="${imageDisplay}" onclick="showAnserImgList(event)">
							<img src="${imageData}" class="img-thumbnail" alt="답변이미지" width="210" height="130" onerror='this.src="${CTX_PATH}/static/images/no_image.jpg"' style="cursor:pointer;">
						</div>
													
						<textarea df-template placeholder="답변 입력" readonly></textarea>
						
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;display:none;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 버튼</h6></label>
								</div>
								<div class="col-4">
									<div class="form-check form-switch">
										<input class="form-check-input" type="checkbox" style="width:45px;" ${gatewayChecked}/>
									</div>
								</div>
							</div>

							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;버튼 상세</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editDetailBtns(event);">편집</button>
									</div>
								</div>
							</div>
							
							<div class="row" style="margin-bottom:3px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;버튼 기본</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="plusBtn(event);">추가</button>
									</div>
								</div>
							</div>
						</div>
						<div id="btnDiv_${obj.nodeId}">
						${buttonAreaHtml}
						</div>
						<div id="gatewayDiv_${obj.nodeId}" style="padding-top:7px;padding-left:40px;">
						${gatewayAreaHtml}
						</div>
					</div>
				</div>
			`;
			nodeHtml = basicNode;
			break;
		
		default:
	}
	return nodeHtml;
}

function saveModelData(){
	var jsonDataObject = editor.export();
	var saveJsonResult = '';

	var saveJsonRoot = new Object();
	var saveJsonNodeArr = new Array();
	saveJsonRoot.nodeArr = saveJsonNodeArr;
	var saveJsonNode;
	var saveJsonPreNodeArr;
	var saveJsonPreNode;
	var saveJsonData;
	var saveJsonDataAttachedArr; 
	var saveJsonDataAttached; 

	for (var e in jsonDataObject.drawflow['Home'].data){
		saveJsonNode = new Object();
		saveJsonNode.nodeId = e;
		saveJsonNode.nodeType = jsonDataObject.drawflow['Home'].data[e].name;
		saveJsonNode.pos_x = jsonDataObject.drawflow['Home'].data[e].pos_x;
		saveJsonNode.pos_y = jsonDataObject.drawflow['Home'].data[e].pos_y;
		if(saveJsonNode.nodeType == 'startNode'){
			saveJsonNode.preNodeArr = null;
		} else {
			saveJsonPreNodeArr = new Array();
			jsonDataObject.drawflow['Home'].data[e].inputs.input_1.connections.forEach((el, t) => {
				saveJsonPreNode = new Object();
				saveJsonPreNode.nodeId = el.node;
				saveJsonPreNode.attachedId = el.input;
				saveJsonPreNodeArr.push(saveJsonPreNode);
			});
			saveJsonNode.preNodeArr = saveJsonPreNodeArr;
		}

		saveJsonData = new Object();
		if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.template)){
			saveJsonData.message = jsonDataObject.drawflow['Home'].data[e].data.template;
		} else {saveJsonData.message = "";}
		if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.image)){
			saveJsonData.image = jsonDataObject.drawflow['Home'].data[e].data.image;
		} else {saveJsonData.image = "";}

		saveJsonDataAttachedArr = new Array();
		var idx = '';
		var sortIdx = 0;
		for (var output in jsonDataObject.drawflow['Home'].data[e].outputs){
			saveJsonDataAttached = new Object();
			jsonDataObject.drawflow['Home'].data[e].outputs[output].connections.forEach((el, t) => {
				idx = output.slice(7);
				saveJsonDataAttached.id = output;

				if(saveJsonNode.nodeType == 'basicNode'){
					if(idx == gatewayBtnIdx){ // gateway link
						saveJsonDataAttached.type = 'link';
						saveJsonDataAttached.label = '';
					} else {
						if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
							if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != null){
								saveJsonDataAttached.data = jsonDataObject.drawflow['Home'].data[e].data.detail[idx];
							} else {
								saveJsonDataAttached.data = null;
							}
						}
						saveJsonDataAttached.type = 'button';
						saveJsonDataAttached.label = jsonDataObject.drawflow['Home'].data[e].data.name[idx];
						if(jsonDataObject.drawflow['Home'].data[e].data.sort != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.sort != null){
							if(typeof jsonDataObject.drawflow['Home'].data[e].data.sort[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.sort[idx] != null){
								saveJsonDataAttached.sort = jsonDataObject.drawflow['Home'].data[e].data.sort[idx];
							} else {
								saveJsonDataAttached.sort = sortIdx;
							}
						} else {
							saveJsonDataAttached.sort = sortIdx;
						}
					}
				} else if(saveJsonNode.nodeType == 'gatewayNode'){
					saveJsonDataAttached.type = 'condition';
					saveJsonDataAttached.label = jsonDataObject.drawflow['Home'].data[e].data.name[idx];
					if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
						if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != null){
							saveJsonDataAttached.data = jsonDataObject.drawflow['Home'].data[e].data.detail[idx];
						} else {
							saveJsonDataAttached.data = null;
						}
					}
					if(jsonDataObject.drawflow['Home'].data[e].data.sort != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.sort != null){
						if(typeof jsonDataObject.drawflow['Home'].data[e].data.sort[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.sort[idx] != null){
							saveJsonDataAttached.sort = jsonDataObject.drawflow['Home'].data[e].data.sort[idx];
						} else {
							saveJsonDataAttached.sort = sortIdx;
						}
					} else {
						saveJsonDataAttached.sort = sortIdx;
					}
				} else if(saveJsonNode.nodeType == 'apiNode'){
					if(idx == gatewayBtnIdx){ // gateway link
						saveJsonDataAttached.type = 'link';
						saveJsonDataAttached.label = '';
						saveJsonDataAttached.sort = 0;
					} else {
						saveJsonDataAttached.type = 'api';
						saveJsonDataAttached.label = jsonDataObject.drawflow['Home'].data[e].data.name[idx];
						if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
							if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != null){
								saveJsonDataAttached.data = jsonDataObject.drawflow['Home'].data[e].data.detail[idx];
							} else {
								saveJsonDataAttached.data = null;
							}
						}
						saveJsonDataAttached.sort = parseInt(idx);
					}
				} else if(saveJsonNode.nodeType == 'startNode'){
					saveJsonDataAttached.type = '';
					saveJsonDataAttached.label = '';
					saveJsonDataAttached.data = '';
					saveJsonDataAttached.sort = sortIdx;
				}
				saveJsonDataAttached.nextNodeId = el.node;
				saveJsonDataAttachedArr.push(saveJsonDataAttached);
				sortIdx++;
			});
			
		}
		
		if(saveJsonNode.nodeType == 'apiNode'){
			var infoObj = new Object();
			infoObj.api = jsonDataObject.drawflow['Home'].data[e].data.api;
			infoObj.param = jsonDataObject.drawflow['Home'].data[e].data.param;
			saveJsonData.infoObj = infoObj;
			
			var checkFlag = false;
			var output = 'output_1';
			idx = output.slice(7);
			for(var apiResIdx in saveJsonDataAttachedArr){
				if(saveJsonDataAttachedArr[apiResIdx].id == output){
					checkFlag = true;
				}
			}
			if(!checkFlag){
				saveJsonDataAttached = new Object();
				saveJsonDataAttached.id = output;
				saveJsonDataAttached.type = 'api';
				saveJsonDataAttached.label = jsonDataObject.drawflow['Home'].data[e].data.name[idx];
				if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
					if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail[idx] != null){
						saveJsonDataAttached.data = jsonDataObject.drawflow['Home'].data[e].data.detail[idx];
					} else {
						saveJsonDataAttached.data = null;
					}
				}
				saveJsonDataAttached.sort = 0;
				saveJsonDataAttached.nextNodeId = null;
				saveJsonDataAttachedArr.push(saveJsonDataAttached);
			}
		}
		
		if(saveJsonNode.nodeType == 'slotNode'){
			var infoObj = new Object();
			infoObj.slot = jsonDataObject.drawflow['Home'].data[e].data.slot;
			saveJsonData.infoObj = infoObj;
		}

		if(saveJsonNode.nodeType == 'metaNode'){
			var infoObj = new Object();
			infoObj.type = jsonDataObject.drawflow['Home'].data[e].data.meta.type;
			infoObj.value = jsonDataObject.drawflow['Home'].data[e].data.meta.value;
			saveJsonData.infoObj = infoObj;
		}

		saveJsonData.attachedArr = saveJsonDataAttachedArr;
		saveJsonNode.data = saveJsonData;
		saveJsonNodeArr.push(saveJsonNode);
	}
	saveJsonResult = JSON.stringify(saveJsonRoot, null, 4);
	return saveJsonResult;
}

function getKeywordData(){
	var jsonDataObject = editor.export();
	var keywords = '';
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'basicNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.template)){
				keywords += jsonDataObject.drawflow['Home'].data[e].data.template + " ";
			}
		} 
	}
	return keywords;
}

function getAnswerImageIds(){
	var jsonDataObject = editor.export();
	var imageArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'basicNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.image)){
				imageArr.push(jsonDataObject.drawflow['Home'].data[e].data.image);
			}
		} 
	}
	return imageArr.join(',');
}

function getDialogModelIds(){
	var jsonDataObject = editor.export();
	var modelArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'metaNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta.type)){
					if(jsonDataObject.drawflow['Home'].data[e].data.meta.type == 'scenario'){
						modelArr.push(jsonDataObject.drawflow['Home'].data[e].data.meta.value);
					}
				}
			}
		} 
	}
	return modelArr.join(',');
}

function getDialogIds(){
	var jsonDataObject = editor.export();
	var dialogArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'metaNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta.type)){
					if(jsonDataObject.drawflow['Home'].data[e].data.meta.type == 'intent'){
						dialogArr.push(jsonDataObject.drawflow['Home'].data[e].data.meta.value);
					}
				}
			}
		} 
	}
	return dialogArr.join(',');
}

function getLinkIds(){
	var jsonDataObject = editor.export();
	var linkArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'metaNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta.type)){
					if(jsonDataObject.drawflow['Home'].data[e].data.meta.type == 'urlLink' || jsonDataObject.drawflow['Home'].data[e].data.meta.type == 'appLink'){
						linkArr.push(jsonDataObject.drawflow['Home'].data[e].data.meta.value);
					}
				}
			}
		} 
	}
	return linkArr.join(',');
}

function getApiIds(){
	var jsonDataObject = editor.export();
	var apiArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'apiNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.api)){
					apiArr.push(jsonDataObject.drawflow['Home'].data[e].data.api);
				}
			}
		} 
	}
	return apiArr.join(',');
}

function getEntityIds(){
	var jsonDataObject = editor.export();
	var entityArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'slotNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.slot)){
					
					for(idx in jsonDataObject.drawflow['Home'].data[e].data.slot){
						entityArr.push(jsonDataObject.drawflow['Home'].data[e].data.slot[idx].entityNo);
					}
				}
			}
		} 
	}
	return entityArr.join(',');
}

function getScriptIds(){
	var jsonDataObject = editor.export();
	var scriptArr = new Array();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'apiNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.param)){
					for(idx in jsonDataObject.drawflow['Home'].data[e].data.param){
						if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.param[idx].script)){
							scriptArr.push(jsonDataObject.drawflow['Home'].data[e].data.param[idx].script);
						}
					}
				}
			}
		} 
	}
	return scriptArr.join(',');
}

function validationBasicNode(){
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'basicNode') {
			console.log(jsonDataObject.drawflow['Home'].data[e].data);
			if(isObjEmpty(jsonDataObject.drawflow['Home'].data[e].data)){
				return false;
			}
			if(typeof jsonDataObject.drawflow['Home'].data[e].data.template == "undefined" || jsonDataObject.drawflow['Home'].data[e].data.template == null || jsonDataObject.drawflow['Home'].data[e].data.template == ''){
				return false;
			}
		} 
	}
	return result;
}

function validationGatewayNode(){
	var result = true;
	var jsonDataObject = editor.export();
	var detailObj;
	var tag = '';
	var regexpArr;
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'gatewayNode') {
			if(jsonDataObject.drawflow['Home'].data[e].data.detail == "undefined" || jsonDataObject.drawflow['Home'].data[e].data.detail == null){
				return false;
			}
			detailObj = jsonDataObject.drawflow['Home'].data[e].data.detail;
			
			var keys = Object.keys(detailObj);
			for(idx in keys) {
				if(keys[idx] != '2'){ // else 조건
					tag = detailObj[keys[idx]].tag;
					regexpArr = detailObj[keys[idx]].regexpArr;
					if((typeof tag == "undefined" || tag == null || tag == "") && (typeof regexpArr == "undefined" || regexpArr == null || regexpArr.length == 0)){
						return false;
					}
				}
			}
		} 
	}
	return result;
}

function validationGatewayNodeLink(){
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'gatewayNode') {
			
			if(typeof jsonDataObject.drawflow['Home'].data[e].outputs == "undefined" || jsonDataObject.drawflow['Home'].data[e].outputs == null){
				return false;
			}
			var keys = Object.keys(jsonDataObject.drawflow['Home'].data[e].outputs);
			for(idx in keys) {
				if(typeof jsonDataObject.drawflow['Home'].data[e].outputs[keys[idx]].connections == "undefined" || jsonDataObject.drawflow['Home'].data[e].outputs[keys[idx]].connections == null){
					return false;
				}
				var length = jsonDataObject.drawflow['Home'].data[e].outputs[keys[idx]].connections.length;
				if(length < 1){
					return false;
				}
			}
		} 
	}
	
	return result;
}

function validationApiNodeLink(){
	var result = true;
	var detailObj;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'apiNode') {
			var length = 0;
			// HAVE TO DO
			
			detailObj = jsonDataObject.drawflow['Home'].data[e].data.detail;
			console.log(detailObj);
			
			var nextNodePortFlag = true;
			if(detailObj[apiBtnExistsIdx].listDataFlag){
				if(detailObj[apiBtnExistsIdx].listData.dataType == 'view'){
					if(typeof detailObj[apiBtnExistsIdx].defLabel == "undefined" || detailObj[apiBtnExistsIdx].defLabel == null || detailObj[apiBtnExistsIdx].defLabel == ''){
						nextNodePortFlag = false;
					}
				} 
			} else {
				if(typeof detailObj[apiBtnExistsIdx].defLabel == "undefined" || detailObj[apiBtnExistsIdx].defLabel == null || detailObj[apiBtnExistsIdx].defLabel == ''){
					nextNodePortFlag = false;
				}
			}
			
			if(nextNodePortFlag){
				// 링크 포트 있음
				length = jsonDataObject.drawflow['Home'].data[e].outputs.output_1.connections.length;
				if(length < 1){
					return false;
				}
			}
			
			length = jsonDataObject.drawflow['Home'].data[e].outputs.output_2.connections.length;
			if(length < 1){
				return false;
			}
			length = jsonDataObject.drawflow['Home'].data[e].outputs.output_3.connections.length;
			if(length < 1){
				return false;
			}
		} 
	}
	
	return result;
}

function validationApiNode(){
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'apiNode') {
			
			if(typeof jsonDataObject.drawflow['Home'].data[e].data.api == "undefined" || jsonDataObject.drawflow['Home'].data[e].data.api == null || jsonDataObject.drawflow['Home'].data[e].data.api == ''){
				return false;
			}
			
			if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail == "undefined" || jsonDataObject.drawflow['Home'].data[e].data.detail == null) {
				return false;
			}
			
			if(typeof jsonDataObject.drawflow['Home'].data[e].data.detail[apiBtnExistsIdx].message == "undefined" || jsonDataObject.drawflow['Home'].data[e].data.detail[apiBtnExistsIdx].message == null || jsonDataObject.drawflow['Home'].data[e].data.detail[apiBtnExistsIdx].message == ''){
				return false;
			}
		}
	}
	return result;
}

function validationSlogNode(){
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'slotNode') {
			if(isEmpty(jsonDataObject.drawflow['Home'].data[e].data.slot)){
				return false;
			}
		}
	}
	return result;
}

function validationSlotNodeLink(){
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'slotNode') {
			var length = 0;
			// HAVE TO DO
			console.log('validationSlotNodeLink');
			console.log(jsonDataObject.drawflow['Home'].data[e].outputs);
			length = jsonDataObject.drawflow['Home'].data[e].outputs.output_1.connections.length;
			if(length < 1){
				return false;
			}
			
			length = jsonDataObject.drawflow['Home'].data[e].outputs.output_2.connections.length;
			if(length < 1){
				return false;
			}
		} 
	}
	
	return result;
}

function validationMetaNode(){
	
	var result = true;
	var jsonDataObject = editor.export();
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		if(jsonDataObject.drawflow['Home'].data[e].name == 'metaNode') {
			if(isEmpty(jsonDataObject.drawflow['Home'].data[e].data.meta)){
				return false;
			}
		}
	}
	return result;
}

function getVariableNameSet(){
	var jsonDataObject = editor.export();
	var detailObj;
	
	var variableNameSet = new Set();
	var variableName = '';
	
	for (var e in jsonDataObject.drawflow['Home'].data) {
		
		if(jsonDataObject.drawflow['Home'].data[e].name == 'gatewayNode' || jsonDataObject.drawflow['Home'].data[e].name == 'basicNode') {
			if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
				detailObj = jsonDataObject.drawflow['Home'].data[e].data.detail;
				var keys = Object.keys(detailObj);
				for(idx in keys) {
					if(typeof detailObj[keys[idx]] != "undefined" && detailObj[keys[idx]] != null){
						variableName = detailObj[keys[idx]].key;
						if(typeof variableName != "undefined" && variableName != null && variableName != ''){
							variableNameSet.add(variableName);						
						}						
					}
				}
			}
		} else if(jsonDataObject.drawflow['Home'].data[e].name == 'apiNode') {
			if(jsonDataObject.drawflow['Home'].data[e].data.detail != "undefined" && jsonDataObject.drawflow['Home'].data[e].data.detail != null){
				detailObj = jsonDataObject.drawflow['Home'].data[e].data.detail;
				
				if(detailObj[apiBtnExistsIdx].defKey != "undefined" && detailObj[apiBtnExistsIdx].defKey != null && detailObj[apiBtnExistsIdx].defKey != ''){
					variableNameSet.add(detailObj[apiBtnExistsIdx].defKey);
				}
				
				if(detailObj[apiBtnExistsIdx].listData != "undefined" && detailObj[apiBtnExistsIdx].listData != null && detailObj[apiBtnExistsIdx].listData != ''){
					if(detailObj[apiBtnExistsIdx].listData.key != "undefined" && detailObj[apiBtnExistsIdx].listData.key != null){
						variableNameSet.add(detailObj[apiBtnExistsIdx].listData.key);
					}
				}
			}
		} else if(jsonDataObject.drawflow['Home'].data[e].name == 'slotNode') {
			if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data)){
				if(!isEmpty(jsonDataObject.drawflow['Home'].data[e].data.slot)){
					for(idx in jsonDataObject.drawflow['Home'].data[e].data.slot){
						variableNameSet.add(jsonDataObject.drawflow['Home'].data[e].data.slot[idx].variableKey);
					}
				}
			}
		}
	}
	
	return variableNameSet;
}

function isEmpty(str){
    if(typeof str == "undefined" || str == null || str == "")
        return true;
    else
        return false ;
}

function updateAllConnectionNodes(){
	var nodeDivId = '';
	$('.parent-node .drawflow-node').each(function (index, item) {
		nodeDivId = $(item).attr('id');
		editor.updateConnectionNodes(nodeDivId);

		var nodeName = editor.getNodeNameCustom(nodeDivId.slice(5));
		$('#btnDiv_'+nodeDivId.slice(5)+' svg').hide();
		$('#' + nodeDivId + ' .fa-window-close').hide();
		$('#' + nodeDivId + ' .fa-pencil-alt').show();
		$('#' + nodeDivId + ' .control-box').hide();
		
		if(nodeName == 'basicNode'){
			if($('#' + nodeDivId + ' .image-box img').attr('src') == CTX_PATH + '/static/images/no_image.jpg'){
				$('#' + nodeDivId + ' .image-box').hide();
			}
			$('#' + nodeDivId + ' .box input[type=text]').attr('readonly',true);
			$('#' + nodeDivId + ' .box textarea').attr('readonly',true);
			$('#' + nodeDivId + ' .box input[type=text]').animate({
				width:"168px"
			},'slow');
			$('#btnDiv_'+nodeDivId.slice(5)+' .output').animate({
				right:"-21px"
			},'slow');
		} else if(nodeName == 'gatewayNode'){
		} else if(nodeName == 'apiNode'){
			$('#' + nodeDivId + ' .fa-edit').hide();
		} 

		// 해당 노드에 대한 링크 위치 업데이트
		editor.updateConnectionNodes(nodeDivId);
		editor.updateHtml(nodeDivId.slice(5));
	});	
}

var id = document.getElementById("drawflow");
const editor = new Drawflow(id);
editor.reroute = true;
editor.draggable_inputs = false;
editor.start();
editor.zoom_refresh();

var startNode = `
<div>
  <div class="title-box"><i class="fas fa-home" ></i> <span style="font-size:1.05rem;">시작노드</span></div>
  <div class="box">
  </div>
</div>
`;

var width = $('#drawflow').width();
var height = $('#drawflow').height();
var correctionLeft = -(width / 10);
var correctionTop = -80;

var startNodeId = editor.addNode('startNode', 0, 1, correctionLeft, correctionTop, 'startNode', {}, startNode );

var curSelectedNodeId;
var curSelectedZIndex = 1000;
var preSelectedZIndex = 2;

// Events!
editor.on('nodeCreated', function(id) {
	console.log("[event] Node created " + id);
})

editor.on('nodeRemoved', function(id) {
	console.log("[event] Node removed " + id);
})

editor.on('nodeSelected', function(id) {
	$('#node-'+curSelectedNodeId).css('z-index',preSelectedZIndex);
	curSelectedNodeId = id;
	$('#node-'+id).css('z-index',curSelectedZIndex);
	console.log("[event] Node selected " + id);
})

editor.on('moduleCreated', function(name) {
	console.log("[event] Module Created " + name);
})

editor.on('moduleChanged', function(name) {
	console.log("[event] Module Changed " + name);
})

editor.on('connectionCreated', function(connection) {
	console.log('[event] Connection created');
	console.log(connection);
})

editor.on('connectionRemoved', function(connection) {
	console.log('[event] Connection removed');
	console.log(connection);
})

editor.on('mouseMove', function(position) {
	//console.log('[event] Position mouse x:' + position.x + ' y:'+ position.y);
})

editor.on('nodeMoved', function(id) {
	console.log("[event] Node moved " + id);
})

editor.on('zoom', function(zoom) {
	console.log('[event] Zoom level ' + zoom);
})

editor.on('translate', function(position) {
	console.log('Translate x:' + position.x + ' y:'+ position.y);
})

editor.on('addReroute', function(id) {
	console.log("[event] Reroute added " + id);
})

editor.on('removeReroute', function(id) {
	console.log("[event] Reroute removed " + id);
})

editor.on('import', function(name) {
	console.log("[event] import " + name);
})

editor.on('load', function(name) {
	console.log("[event] load " + name);
	updateAllConnectionNodes();
})

/* DRAG EVENT */

/* Mouse and Touch Actions */

var elements = document.getElementsByClassName('drag-drawflow');
for (var i = 0; i < elements.length; i++) {
	elements[i].addEventListener('touchend', drop, false);
	elements[i].addEventListener('touchmove', positionMobile, false);
	elements[i].addEventListener('touchstart', drag, false );
}

var mobile_item_selec = '';
var mobile_last_move = null;
function positionMobile(ev) {
	mobile_last_move = ev;
}

function allowDrop(ev) {
	ev.preventDefault();
}

function drag(ev) {
	if (ev.type === "touchstart") {
		mobile_item_selec = ev.target.closest(".drag-drawflow").getAttribute('data-node');
	} else {
		ev.dataTransfer.setData("node", ev.target.getAttribute('data-node'));
	}
}

function drop(ev) {
	if (ev.type === "touchend") {
		var parentdrawflow = document.elementFromPoint( mobile_last_move.touches[0].clientX, mobile_last_move.touches[0].clientY).closest("#drawflow");
		if(parentdrawflow != null) {
		  addNodeToDrawFlow(mobile_item_selec, mobile_last_move.touches[0].clientX, mobile_last_move.touches[0].clientY);
		}
		mobile_item_selec = '';
	} else {
		ev.preventDefault();
		var data = ev.dataTransfer.getData("node");
		addNodeToDrawFlow(data, ev.clientX, ev.clientY);
	}
}

function addNodeToDrawFlow(name, pos_x, pos_y) {
	if(editor.editor_mode === 'fixed') {
		return false;
	}
	pos_x = pos_x * ( editor.precanvas.clientWidth / (editor.precanvas.clientWidth * editor.zoom)) - (editor.precanvas.getBoundingClientRect().x * ( editor.precanvas.clientWidth / (editor.precanvas.clientWidth * editor.zoom)));
	pos_y = pos_y * ( editor.precanvas.clientHeight / (editor.precanvas.clientHeight * editor.zoom)) - (editor.precanvas.getBoundingClientRect().y * ( editor.precanvas.clientHeight / (editor.precanvas.clientHeight * editor.zoom)));

	switch (name) {
		case 'metaNode':
			var metaNode = `
				<div>            
					<div class="title-box"><i class="fas fa-code" ></i> <span style="font-size:1.05rem;">메타정보노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:56px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:56px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
					</div>
					
				</div>
			`;
			var nodeId = editor.addNode('metaNode', 1, 0, pos_x, pos_y, 'metaNode', {}, metaNode );

			var metaId = 'meta_' + nodeId;
			var metaTypeId = 'metatype_' + nodeId;
			var metaNameId = 'metaname_' + nodeId;
			var metaContId = 'metacont_' + nodeId; 
			
			var metaAreaHtml = `
				<div class="col-md-12" style="margin-bottom:10px;">
					<button type="button" id="${metaNameId}" class="btn d-flex btn-light-secondary w-100 d-block text-secondary font-weight-medium">
					  메타정보<span class="badge ms-auto bg-secondary" style="top:2px;">타입</span>
					</button>
				</div>
				<textarea df-template id="${metaContId}" style="height:60px;" disabled></textarea>
				<input type="hidden" id="${metaId}" value=""/>
				<input type="hidden" id="${metaTypeId}" value=""/>
				<div class="control-box" style="display:none;">
					<div class="row" style="margin-top:5px;">
						<div class="col-8">
							<label style="margin-top:5px;"><h6>&nbsp;메타 정보명</h6></label>
						</div>
						<div class="col-4">
							<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
								<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'metaNode', 'meta');">편집</button>
							</div>
						</div>
					</div>
				</div>
				`;
			
			$('#node-'+nodeId+' .box').append(metaAreaHtml);
			editor.updateHtml(nodeId);

			break;
		case 'apiNode':
			var apiNode = `
				<div>            
					<div class="title-box"><i class="fas fa-cogs" ></i> <span style="font-size:1.05rem;">API노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:90px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:90px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
					</div>
					
				</div>
			`;
			var nodeId = editor.addNode('apiNode', 1, 0, pos_x, pos_y, 'apiNode', {}, apiNode );
			
			var apiId = 'api_' + nodeId;
			var apiNameId = 'apiname_' + nodeId;
						
			var apiInfoAreaHtml = `
				<div class="col-md-12" style="margin-bottom:10px;">
					<button type="button" id="${apiNameId}" class="btn d-flex btn-light-secondary w-100 d-block text-secondary font-weight-medium">API 정보</button>
				</div>
				<input type="hidden" id="${apiId}" value=""/>
				<div class="control-box" style="display:none;">
					<div class="row" style="margin-top:5px;display:none;">
						<div class="col-8">
							<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 버튼</h6></label>
						</div>
						<div class="col-4">
							<div class="form-check form-switch">
								<input class="form-check-input" type="checkbox" style="width:45px;"/>
							</div>
						</div>
					</div>
							
					<div class="row" style="margin-top:5px;">
						<div class="col-8">
							<label style="margin-top:5px;"><h6>&nbsp;1. API 선택</h6></label>
						</div>
						<div class="col-4">
							<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
								<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'apiNode', 'apiReq');">편집</button>
							</div>
						</div>
					</div>
					<div class="row" style="margin-top:5px;margin-bottom:10px;">
						<div class="col-8">
							<label style="margin-top:5px;"><h6>&nbsp;2. API 응답 조건</h6></label>
						</div>
						<div class="col-4">
							<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
								<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editLableBtn(event, 'apiNode', 'apiRes');">편집</button>
							</div>
						</div>
					</div>
				</div>
				`;
			
			$('#node-'+nodeId+' .box').append(apiInfoAreaHtml);
			editor.updateHtml(nodeId);
			
			var btnOutIdx = apiBtnExistsIdx;
			var btnId = '';
			var btnClass = '';
			
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonPreAreaHtml = `
				<div id="btnDiv_pre_${nodeId}" class="apiResCondition">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="결과가 있을 경우" data-btnId="${btnId}" disabled>결과가 있을 경우</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonPreAreaHtml);
			
			var buttonAreaHtml = `
				<div id="btnDiv_${nodeId}" class="apiResCondition"></div>
				`;
			$('#node-'+nodeId+' .box').append(buttonAreaHtml);
			
			btnOutIdx = apiBtnNotExistsIdx;
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonPostAreaHtml = `
				<div id="btnDiv_post_${nodeId}" class="apiResCondition">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="결과가 없을 경우" data-btnId="${btnId}" disabled>결과가 없을 경우</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonPostAreaHtml);
			editor.updateHtml(nodeId);
			
			btnOutIdx = apiBtnExceptionIdx;
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonExceptionAreaHtml = `
				<div id="btnDiv_exception_${nodeId}" class="apiResCondition">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="예외발생 경우" data-btnId="${btnId}" disabled>예외발생 경우</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonExceptionAreaHtml);
			editor.updateHtml(nodeId);

			var gatewayAreaHtml = `
				<div id="gatewayDiv_${nodeId}" style="padding-top:7px;padding-left:40px;"></div>
				`;
			$('#node-'+nodeId+' .box').append(gatewayAreaHtml);
			editor.updateHtml(nodeId);
			editor.addNodeOutput(nodeId, apiBtnExistsIdx);
			editor.addNodeOutput(nodeId, apiBtnNotExistsIdx);
			editor.addNodeOutput(nodeId, apiBtnExceptionIdx);
			$('#btnDiv_pre_'+nodeId+' .output').css('right','-18px');
			$('#btnDiv_post_'+nodeId+' .output').css('right','-18px');
			$('#btnDiv_exception_'+nodeId+' .output').css('right','-18px');

			break;
		case 'gatewayNode':
			var gatewayNode = `
				<div>            
					<div class="title-box"><i class="fas fa-code-branch" ></i> <span style="font-size:1.05rem;">응답게이트웨이노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:12px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:12px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 조건</h6></label>
								</div>
								<div class="col-4">
									<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editConditaionBtn(event);">편집</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			`;
			var nodeId = editor.addNode('gatewayNode', 1, 0, pos_x, pos_y, 'gatewayNode', {}, gatewayNode );
			
			var btnOutIdx = conditionBtnIfIdx;
			var btnId = '';
			var btnClass = '';
			
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonPreAreaHtml = `
				<div id="btnDiv_pre_${nodeId}">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="IF 조건" data-btnId="${btnId}" disabled>IF 조건</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonPreAreaHtml);
			
			var buttonAreaHtml = `
				<div id="btnDiv_${nodeId}"></div>
				`;
			$('#node-'+nodeId+' .box').append(buttonAreaHtml);

			btnOutIdx = conditionBtnElseIdx;
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonPostAreaHtml = `
				<div id="btnDiv_post_${nodeId}">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="ELSE 조건" data-btnId="${btnId}"  disabled>ELSE 조건</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonPostAreaHtml);
			editor.updateHtml(nodeId);
			editor.addNodeOutput(nodeId, conditionBtnIfIdx);
			editor.addNodeOutput(nodeId, conditionBtnElseIdx);
			
			$('#btnDiv_pre_'+nodeId+' .output').css('right','-18px');
			$('#btnDiv_post_'+nodeId+' .output').css('right','-18px');

			break;
		case 'slotNode':
			var slotNode = `
				<div>            
					<div class="title-box"><i class="fas fa-sync" ></i> <span style="font-size:1.05rem;">SLOT노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:78px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:78px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;슬롯 필링</h6></label>
								</div>
								<div class="col-4">
									<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editSlotBtn(event);">편집</button>
								</div>
							</div>
						</div>
					</div>
				</div>
			`;
			var nodeId = editor.addNode('slotNode', 1, 0, pos_x, pos_y, 'slotNode', {}, slotNode );
			
			var btnOutIdx = slotBtnSuccessIdx;
			var btnId = '';
			var btnClass = '';
			
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonSuccessAreaHtml = `
				<div id="btnDiv_success_${nodeId}">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="SUCCESS" data-btnId="${btnId}" disabled>슬롯필링 성공할 경우</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonSuccessAreaHtml);

			btnOutIdx = slotBtnFailIdx;
			btnId = 'btn_' + nodeId + '_' + btnOutIdx;
			btnClass = 'button-' + btnOutIdx;
			var buttonFailAreaHtml = `
				<div id="btnDiv_fail_${nodeId}">
					<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
						<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
							<div>
								<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:170px;" data-toggle="popover" data-trigger="focus" title="FAIL" data-btnId="${btnId}"  disabled>슬롯필링 실패할 경우</button>
							</div>
						</div>
					</div>
				</div>
				`;
			$('#node-'+nodeId+' .box').append(buttonFailAreaHtml);
			editor.updateHtml(nodeId);
			editor.addNodeOutput(nodeId, slotBtnSuccessIdx);
			editor.addNodeOutput(nodeId, slotBtnFailIdx);
			
			$('#btnDiv_success_'+nodeId+' .output').css('right','-18px');
			$('#btnDiv_fail_'+nodeId+' .output').css('right','-18px');

			break;
		case 'basicNode':
			var basicNode = `
				<div>            
					<div class="title-box"><i class="fas fa-comment" ></i> <span style="font-size:1.05rem;">기본답변노드</span> <i class="fas fa-pencil-alt fa-lg" onclick="btnEdit(event);" style="margin-left:58px;cursor:pointer;"></i><i class="fas fa-window-close fa-lg" onclick="btnEditSave(event);" style="margin-left:58px;cursor:pointer;display:none;"></i></div>
					
					<div class="box">
						<div class="image-box" style="display:none;" onclick="showAnserImgList(event)">
							<img src="" class="img-thumbnail" alt="답변이미지" width="210" height="130" onerror='this.src="${CTX_PATH}/static/images/no_image.jpg"' style="cursor:pointer;">
						</div>
									
						<textarea df-template placeholder="답변 입력" readonly></textarea>
						
						<div class="control-box" style="display:none;">
							<div class="row" style="margin-top:5px;margin-bottom:5px;display:none;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;게이트웨이 버튼</h6></label>
								</div>
								<div class="col-4">
									<div class="form-check form-switch">
										<input class="form-check-input" type="checkbox" style="width:45px;"/>
									</div>
								</div>
							</div>

							<div class="row" style="margin-top:5px;margin-bottom:5px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;버튼 상세</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="editDetailBtns(event);">편집</button>
									</div>
								</div>
							</div>
							
							<div class="row" style="margin-bottom:3px;">
								<div class="col-8">
									<label style="margin-top:5px;"><h6>&nbsp;버튼 기본</h6></label>
								</div>
								<div class="col-4">
									<div class="btn-group" role="group" aria-label="Button group with nested dropdown">
										<button type="button" class="btn btn-light-secondary btn-sm text-secondary font-weight-medium" onclick="plusBtn(event);">추가</button>
									</div>
								</div>
							</div>
						</div>

					</div>
					
				</div>
			`;
			var nodeId = editor.addNode('basicNode', 1, 0, pos_x, pos_y, 'basicNode', {}, basicNode );
			var buttonAreaHtml = `
				<div id="btnDiv_${nodeId}"></div>
				`;
			$('#node-'+nodeId+' .box').append(buttonAreaHtml);
			var gatewayAreaHtml = `
				<div id="gatewayDiv_${nodeId}" style="padding-top:7px;padding-left:40px;"></div>
				`;
			$('#node-'+nodeId+' .box').append(gatewayAreaHtml);
			editor.updateHtml(nodeId);

			break;
		
		default:
	}
}

/*
노드 버튼 정렬
*/
function btnSortEnable(){
	var correction = 0;
	var b = 1;
	var nodeName = editor.getNodeNameCustom(curSelectedNodeId);
	if(nodeName == 'basicNode'){
		b = 4.5;
	} else if(nodeName == 'gatewayNode'){
		b = 1.2;
	}
	$('#btnDiv_'+curSelectedNodeId).sortable({
		forcePlaceholderSize: true,
		placeholder: "ph",
		update: function(event, ui) {  
			// 해당 노드에 대한 링크 위치 업데이트
			editor.updateConnectionNodes("node-" + curSelectedNodeId);
			//console.log('[btnSortEnable] sort update');
			var nodeBtnIdArr = $(this).sortable('toArray');
			nodeBtnIdArr.forEach((a, t) => {
				editor.updateNodeOutputSort(curSelectedNodeId, a.substring(a.lastIndexOf("_")+1), t);
			});
			console.log('[btnSortEnable] sort ' + nodeBtnIdArr);
		},
		start: function(event, ui) {
			console.log('[btnSortEnable] start');
			if(editor.zoom == 1){
				correction = 0;
			} else if(editor.zoom < 1){
				correction = (1 - editor.zoom) * 100 * b;
			} else if(editor.zoom > 1){
				correction = (editor.zoom - 1) * (-100) * b;
			}
		},
		sort: function(event, ui) {     
			//console.log('[btnSortEnable] sort start >>> correction : ' + correction + ', ui.position.top : ' + ui.position.top); 
			ui.helper.css({'top' : ui.position.top + correction + 'px'});
			// 해당 노드에 대한 링크 위치 업데이트
			editor.updateConnectionNodes("node-" + curSelectedNodeId);
		}
	});
	$('#btnDiv_'+curSelectedNodeId).disableSelection();
	$('#btnDiv_'+curSelectedNodeId).sortable('enable'); 
}

/*
노드 버튼 정렬 비활성화
*/
function btnSortDisable(){
	$('#btnDiv_'+curSelectedNodeId).sortable('disable'); 
}

/*
게이트웨이 버튼 이벤트 등록
*/
function btnGateWayEnable(nodeDivId){
	$('#'+ nodeDivId + ' .box input[type=checkbox]').change(function(event){
		var flag = $(this).is(':checked');
		if(flag){
			// 게이트웨이 버튼 활성화
			createGateway(event);
		} else {
			// 게이트웨이 버튼 비활성화
			removeGateway(event);
		}
		event.preventDefault();
	});
}

function btnGateWayDisable(nodeDivId){
	$('#'+ nodeDivId + ' .box input[type=checkbox]').off('change');
}

/*
노드 편집 모드
*/
function btnEdit(e) {
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[btnEdit] nodeDivId is ' + nodeDivId);

	if(editor.editor_mode == 'fixed'){
		return false;
	}

	var nodeName = editor.getNodeNameCustom(curSelectedNodeId);

	editor.editor_mode = 'fixed';
	$('#btnDiv_'+curSelectedNodeId+' svg').show();
	$('#' + nodeDivId + ' .fa-pencil-alt').hide();
	$('#' + nodeDivId + ' .fa-window-close').show();
	$('#' + nodeDivId + ' .control-box').show();
	
	$('#drawflow').css('background-size','25px 25px');
		
	if(nodeName == 'basicNode'){
		$('#' + nodeDivId + ' .image-box').show();
		$('#' + nodeDivId + ' .box input[type=text]').attr('readonly',false);
		$('#' + nodeDivId + ' .box textarea').attr('readonly',false);
		$('#' + nodeDivId + ' .box input[type=text]').animate({
			width:"150px"
		},'slow');
		$('#btnDiv_'+curSelectedNodeId+' .output').animate({
			right:"-39px"
		},'slow');
		$('#gatewayDiv_'+curSelectedNodeId+' .output').animate({
			right:"-39px"
		},'slow');
		btnSortEnable();
		btnGateWayEnable(nodeDivId);
	} else if(nodeName == 'gatewayNode'){
		btnSortEnable();
		$('#' + nodeDivId + ' .box .btn.waves-effect').animate({
			width:"150px"
		},'slow');
		$('#btnDiv_'+curSelectedNodeId+' .output').animate({
			right:"-39px"
		},'slow');
		$('#btnDiv_pre_'+curSelectedNodeId+' .output').animate({
			right:"-39px"
		},'slow');
		$('#btnDiv_post_'+curSelectedNodeId+' .output').animate({
			right:"-39px"
		},'slow');
		$('#' + nodeDivId + ' .box button').attr("disabled", false);
		setGatewayPopoverEvent();
	} else if(nodeName == 'apiNode'){
		btnGateWayEnable(nodeDivId);
		$('#' + nodeDivId + ' .box .apiResCondition button').attr("disabled", false);
		setApiResPopoverEvent();
	} 
	
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
}

/*
노드 버튼 편집 완료
*/
function btnEditSave(e) {
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[btnEditSave] nodeDivId is ' + nodeDivId);

	var nodeName = editor.getNodeNameCustom(curSelectedNodeId);
	
	if(nodeName == 'basicNode'){
		var btnLabelLength = $('#btnDiv_'+curSelectedNodeId+' input').length;
		var btnLabel = '';
		for(var i=0; i<btnLabelLength; i++){
			btnLabel = $('#btnDiv_'+curSelectedNodeId+' input').eq(i).val();	
			if(typeof btnLabel == "undefined" || btnLabel == null || btnLabel == ""){
				console.log('[btnEditSave] btnLabel is ' + btnLabel);
				Swal.fire({
					type: "warning",
					title: "버튼 편집",
					text: "버튼 라벨은 필수 입력입니다.",
				  }).then((result) => {		
					  setTimeout(() => $('#btnDiv_'+curSelectedNodeId+' input').eq(i).focus(), 500);
				  });
				return;
			}
		}
	}

	editor.editor_mode = 'edit';
	$('#btnDiv_'+curSelectedNodeId+' svg').hide();
	$('#' + nodeDivId + ' .fa-window-close').hide();
	$('#' + nodeDivId + ' .fa-pencil-alt').show();
	$('#' + nodeDivId + ' .control-box').hide();
	
	$('#drawflow').css('background-size','');
	
	if(nodeName == 'basicNode'){
		if($('#' + nodeDivId + ' .image-box img').attr('src') == CTX_PATH + '/static/images/no_image.jpg'){
			$('#' + nodeDivId + ' .image-box').hide();
		}
		$('#' + nodeDivId + ' .box input[type=text]').attr('readonly',true);
		$('#' + nodeDivId + ' .box textarea').attr('readonly',true);
		$('#' + nodeDivId + ' .box input[type=text]').animate({
			width:"168px"
		},'slow');
		$('#btnDiv_'+curSelectedNodeId+' .output').animate({
			right:"-21px"
		},'slow');
		btnSortDisable();
		btnGateWayDisable(nodeDivId);
	} else if(nodeName == 'gatewayNode'){
		btnSortDisable();
		$('#' + nodeDivId + ' .box .btn.waves-effect').animate({
			width:"170px"
		},'slow');
		$('#btnDiv_'+curSelectedNodeId+' .output').animate({
			right:"-18px"
		},'slow');
		$('#btnDiv_pre_'+curSelectedNodeId+' .output').animate({
			right:"-18px"
		},'slow');
		$('#btnDiv_post_'+curSelectedNodeId+' .output').animate({
			right:"-18px"
		},'slow');
		$('#' + nodeDivId + ' .box button').attr("disabled", true);
	} else if(nodeName == 'apiNode'){
		btnGateWayDisable(nodeDivId);
		$('#' + nodeDivId + ' .box .apiResCondition button').attr("disabled", true);
	} 
	
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
	editor.updateHtml(curSelectedNodeId);
}

/*
노드 버튼 추가
*/
function plusBtn(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[plusBtn] nodeDivId is ' + nodeDivId);
	
	var btnOutIdx = 1;
	var className = '';
	var idx = 0;
	$('#btnDiv_'+curSelectedNodeId).children().each(function(){
		className = $(this).attr('class');
		idx = className.substring('button-'.length);
		if(parseInt(idx) >= btnOutIdx){
			btnOutIdx = parseInt(idx) + 1;
		}
	});
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var btnClass = 'button-' + btnOutIdx;
	console.log('[plusBtn] btnId : ' + btnId + ', btnOutIdx : ' + btnOutIdx);

	var buttonHtml = `
	<div class="${btnClass}" id="${btnId}" style="display:flex;align-items:center;padding-top:7px;">
		<i class="fas fa-sort fa-lg" style="cursor:pointer;"></i><input type="text" df-name-${btnOutIdx} placeholder="버튼라벨" value=""/><i class="fas fa-minus" onclick="deleteBtn(event,'${btnId}','${btnOutIdx}');" style="left:187px;cursor:pointer;"></i>
	</div>
	`;
	$('#btnDiv_'+curSelectedNodeId).append(buttonHtml);
	editor.addNodeOutput(curSelectedNodeId, btnOutIdx);
}

/*
노드 버튼 추가 (버튼라벨/변수명/변수값)
*/
function plusBtnDetail(btnLabel, btnKey, btnValue){
	
	var btnOutIdx = 1;
	var className = '';
	var idx = 0;
	$('#btnDiv_'+curSelectedNodeId).children().each(function(){
		className = $(this).attr('class');
		idx = className.substring('button-'.length);
		if(parseInt(idx) >= btnOutIdx){
			btnOutIdx = parseInt(idx) + 1;
		}
	});
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var btnClass = 'button-' + btnOutIdx;
	console.log('[plusBtnDetail] btnId : ' + btnId + ', btnOutIdx : ' + btnOutIdx);

	var buttonHtml = `
	<div class="${btnClass}" id="${btnId}" style="display:flex;align-items:center;padding-top:7px;">
		<i class="fas fa-sort fa-lg" style="cursor:pointer;"></i><input type="text" df-name-${btnOutIdx} placeholder="버튼라벨" value="${btnLabel}"/><i class="fas fa-minus" onclick="deleteBtn(event,'${btnId}','${btnOutIdx}');" style="left:187px;cursor:pointer;"></i>
	</div>
	`;
	$('#btnDiv_'+curSelectedNodeId).append(buttonHtml);
	editor.addNodeOutput(curSelectedNodeId, btnOutIdx);
	console.log('[plusBtnDetail] curSelectedNodeId : ' + curSelectedNodeId + ', btnOutIdx : ' + btnOutIdx + ', btnLabel : ' + btnLabel + ', btnKey : ' + btnKey + ', btnValue : ' + btnValue);
	var detailDataObj = new Object();
	detailDataObj.key = btnKey;
	detailDataObj.value = btnValue;
	detailDataObj.tag = null;
	detailDataObj.regexpArr = null;
	editor.updateNodeOutputDetail(curSelectedNodeId, btnOutIdx, btnLabel, detailDataObj);
}

/*
노드 버튼 갱신 (ID/버튼라벨/변수명/변수값)
*/
function updateBtnDetail(id, btnLabel, btnKey, btnValue){
	var btnId = 'btn_' + curSelectedNodeId + '_' + id;
	$('#' + btnId + ' input').val(btnLabel);
	
	var detailDataObj = new Object();
	detailDataObj.key = btnKey;
	detailDataObj.value = btnValue;
	detailDataObj.tag = null;
	detailDataObj.regexpArr = null;
	editor.updateNodeOutputDetail(curSelectedNodeId, id, btnLabel, detailDataObj);
}

/*
게이트웨이 버튼 생성
*/
function createGateway(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	//console.log('[createGateway] nodeDivId is ' + nodeDivId);

	var btnOutIdx = gatewayBtnIdx;
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var btnClass = 'button-' + btnOutIdx;
	console.log('[plusGateway] btnId : ' + btnId + ', btnOutIdx : ' + btnOutIdx);

	var buttonHtml = `
	<div class="${btnClass}" id="${btnId}" style="display:flex;align-items:center;">

		<div class="row justify-content-center">
			<div class="col-md-12 col-lg-12">
				<button type="button" class="btn waves-effect waves-light btn-secondary" disabled>게이트웨이 연결</button>
			</div>
		</div>

	</div>
	`;
	$('#gatewayDiv_'+curSelectedNodeId).html(buttonHtml);
	editor.addNodeOutput(curSelectedNodeId, btnOutIdx);
}

/*
게이트웨이 버튼 삭제
*/
function removeGateway(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[removeGateway] nodeDivId is ' + nodeDivId);	
	var btnOutIdx = gatewayBtnIdx;
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var outputClassName = 'output_' + btnOutIdx;
	editor.removeNodeOutputCustom(curSelectedNodeId,outputClassName);
	$('#' + btnId).remove();
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
}

/*
버튼 상세 편집
*/
function editDetailBtns(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[editDetailBtns] nodeDivId is ' + nodeDivId);
	$('#detailButton_fields').html('');
	var btnKey = '';
	var btnValue = '';
	var nodeObj = editor.getNodeFromId(curSelectedNodeId);
	var curBtnObj = nodeObj.data.name;
	var curBtnSortObj = nodeObj.data.sort;
	var curBtnDetailObj = nodeObj.data.detail;
	
	var dataArr = new Array();
	var data;
	
	if(typeof curBtnObj != "undefined" && curBtnObj != null){
		var keys = Object.keys(curBtnObj);
		for(idx in keys) {
			data = new Object();
			btnKey = '';
			btnValue = '';
			if(typeof curBtnDetailObj != "undefined" && curBtnDetailObj != null){
				if(typeof curBtnDetailObj[keys[idx]] != "undefined" && curBtnDetailObj[keys[idx]] != null){
					btnKey = curBtnDetailObj[keys[idx]].key;
					btnValue = curBtnDetailObj[keys[idx]].value;
				}				
			}
			data.id = keys[idx];
			data.label = curBtnObj[keys[idx]];
			data.btnKey = btnKey;
			data.btnValue = btnValue;
			
			if(typeof curBtnSortObj == "undefined" || curBtnSortObj == null){
				data.sort = idx;
			} else {
				data.sort = curBtnSortObj[keys[idx]];
			}
			dataArr.push(data);
		}		
		dataArr.sort(function (a, b) { 
			return a.sort < b.sort ? -1 : a.sort > b.sort ? 1 : 0;  
		});
		for(idx in dataArr){
			detailButton_fields(dataArr[idx].id, dataArr[idx].label, dataArr[idx].btnKey, dataArr[idx].btnValue);		
		}
	}
	
	$("#entityBtnSrch").val(null).trigger('change');
	$('#detailBtns-modal').modal('show');
}

/*
노드 라벨 버튼 편집
*/
function editLableBtn(e, nodeName, detail){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	
	if(nodeName == 'apiNode'){
		if(detail == 'apiReq'){
			$('#api-req-modal-varname').html('');
			var currentApiId = $('#api_' + curSelectedNodeId).val();
			
			var variableNameSet = getVariableNameSet();
			var variableNameArr = Array.from(variableNameSet);
			var html = '';
			if(variableNameArr.length > 0){
				for(idx in variableNameArr){
					html += '<li class="list-group-item d-flex align-items-center">';
					html += '<i class="me-2 mdi mdi-information-variant"></i>';
					html += variableNameArr[idx];	
					html += '</li>';				
				}
			} else {
				html += '<li class="list-group-item d-flex align-items-center">';
				html += '시나리오 컨텍스트 변수가 없습니다';
				html += '</li>';
			}
			$('#api-req-modal-varname').html(html);
			if(typeof currentApiId == "undefined" || currentApiId == null || currentApiId == ''){
				$("#apiBtnSrch").val(null).trigger('change');
			} else {
				$("#apiBtnSrch").val(currentApiId).trigger('change');
				
				var nodeObj = editor.getNodeFromId(curSelectedNodeId);
				var paramArr = nodeObj.data.param;
				$('#api_fields').html('');
				for(idx in paramArr){
					api_fields(paramArr[idx].name, paramArr[idx].mapping, paramArr[idx].script);
				}
			}
			$('#api-rep-modal').modal('show');
		} else if(detail == 'apiRes'){
			// apiReq 팝업 편집이 완료되었는지 확인
			var dataObj = editor.getNodeOutputData(curSelectedNodeId);
			if(typeof dataObj.api == "undefined" || dataObj.api == null || dataObj.api == ''){
				Swal.fire({
					type: "error",
					title: "API 선택",
					text: "API 응답 조건을 편집하기 위해서는 API 선택이 선행 되어야 합니다.",
				  });
				return false;
			}
			// modal set start
			initApiResModal();
			setApiResult(dataObj.api);
			// 모달 데이터 세팅 : 신규인지 수정인지에 따라 데이터 세팅
			if(!(typeof dataObj.detail == "undefined" || dataObj.detail == null)){
				console.log(dataObj.detail[apiBtnExceptionIdx]);
				$('#resultStatusConditionType').val(dataObj.detail[apiBtnExceptionIdx].conditionType);
				$('#resultStatusCondition').val(dataObj.detail[apiBtnExceptionIdx].condition);
				setOperatorSelectBox('resultStatusOperator', dataObj.detail[apiBtnExceptionIdx].conditionType);
				$('#resultStatusOperator').val(dataObj.detail[apiBtnExceptionIdx].operator).trigger('change');
				
				if(dataObj.detail[apiBtnExceptionIdx].conditionType == 'json-string' && (dataObj.detail[apiBtnExceptionIdx].operator == 'eq' || dataObj.detail[apiBtnExceptionIdx].operator =='noteq')){
					$('#resultStatusStringValue').val(dataObj.detail[apiBtnExceptionIdx].conditionValue);
				} else if(dataObj.detail[apiBtnExceptionIdx].conditionType == 'json-literal number'){
					$('#resultStatusIntValue').val(dataObj.detail[apiBtnExceptionIdx].conditionValue);
				} else if(dataObj.detail[apiBtnExceptionIdx].conditionType == 'json-array' && !(dataObj.detail[apiBtnExceptionIdx].operator == 'isnull' || dataObj.detail[apiBtnExceptionIdx].operator == 'isnotnull')){
					$('#resultStatusIntValue').val(dataObj.detail[apiBtnExceptionIdx].conditionValue);
				} else {
					// nothing to do...
				}
				
				$('#resultExistsConditionType').val(dataObj.detail[apiBtnNotExistsIdx].conditionType);
				$('#resultExistsCondition').val(dataObj.detail[apiBtnNotExistsIdx].condition);
				setOperatorSelectBox('resultExistsOperator', dataObj.detail[apiBtnNotExistsIdx].conditionType);
				$('#resultExistsOperator').val(dataObj.detail[apiBtnNotExistsIdx].operator).trigger('change');
				
				if(dataObj.detail[apiBtnNotExistsIdx].conditionType == 'json-string' && (dataObj.detail[apiBtnNotExistsIdx].operator == 'eq' || dataObj.detail[apiBtnNotExistsIdx].operator =='noteq')){
					$('#resultExistsStringValue').val(dataObj.detail[apiBtnNotExistsIdx].conditionValue);
				} else if(dataObj.detail[apiBtnNotExistsIdx].conditionType == 'json-literal number'){
					$('#resultExistsIntValue').val(dataObj.detail[apiBtnNotExistsIdx].conditionValue);
				} else if(dataObj.detail[apiBtnNotExistsIdx].conditionType == 'json-array' && !(dataObj.detail[apiBtnNotExistsIdx].operator == 'isnull' || dataObj.detail[apiBtnNotExistsIdx].operator == 'isnotnull')){
					$('#resultExistsIntValue').val(dataObj.detail[apiBtnNotExistsIdx].conditionValue);
				} else {
					// nothing to do...
				}
				
				$('#resultUseMessage').val(dataObj.detail[apiBtnExistsIdx].message);
				
				var resultUseMappingInfo = dataObj.detail[apiBtnExistsIdx].mappingInfo;
				var resultUseListDataFlag = dataObj.detail[apiBtnExistsIdx].listDataFlag;
				var listData = dataObj.detail[apiBtnExistsIdx].listData;
				
				var keys = Object.keys(resultUseMappingInfo);
				for(idx in keys) {
					result_use_variables_fields(keys[idx], resultUseMappingInfo[keys[idx]]);
				}
				
				if(resultUseListDataFlag){
					$('#resultUseListDataFlag').prop("checked", true).trigger('change');
					$('#resultUseListDataType').val(listData.dataType).trigger('change');
					$('#resultUseListMaxCount').val(listData.listMaxCount);
					$('#resultUseListSelector').val(listData.listSelector);
					$('#resultUseViewLabel').val(listData.viewLabel);
					if(listData.dataType == 'button'){
						$('#resultUseButtonLabel').val(listData.label);
						$('#resultUseButtonKey').val(listData.key);
						$('#resultUseButtonValue').val(listData.value);	
					} else if(listData.dataType == 'view'){
						$('#resultUseDefButtonLabel').val(dataObj.detail[apiBtnExistsIdx].defLabel);
						$('#resultUseDefButtonKey').val(dataObj.detail[apiBtnExistsIdx].defKey);
						$('#resultUseDefButtonValue').val(dataObj.detail[apiBtnExistsIdx].defValue);
					}
				} else {
					$('#resultUseDefButtonLabel').val(dataObj.detail[apiBtnExistsIdx].defLabel);
					$('#resultUseDefButtonKey').val(dataObj.detail[apiBtnExistsIdx].defKey);
					$('#resultUseDefButtonValue').val(dataObj.detail[apiBtnExistsIdx].defValue);
				}
			}
			$('#api-res-modal').modal('show');
			//setApiPopoverEvent();
			// modal set end
		}
	} else if(nodeName == 'metaNode'){
		var currentMetaId = $('#meta_' + curSelectedNodeId).val();
		var currentMetaTypeId = $('#metatype_' + curSelectedNodeId).val();
		if(typeof currentMetaId == "undefined" || currentMetaId == null || currentMetaId == ''){
			$('#metaTypeValue').val(null).trigger('change');
			$("#urlLinkValue").val(null).trigger('change');
			$("#appLinkValue").val(null).trigger('change');
			$("#scenarioValue").val(null).trigger('change');
			$("#intentValue").val(null).trigger('change');
			$('#userDefineValue').val('');
			$('#phoneLinkValue').val('');
		} else {
			console.log('[editLableBtn] currentMetaId : ' + currentMetaId + ', currentMetaTypeId : ' + currentMetaTypeId);
			// 값 세팅
			$('#metaTypeValue').val(currentMetaTypeId).trigger('change');
			if(currentMetaTypeId == 'urlLink'){
				$("#urlLinkValue").val(currentMetaId).trigger('change')
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(2).show();
			} else if(currentMetaTypeId == 'appLink'){
				$("#appLinkValue").val(currentMetaId).trigger('change')
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(3).show();
			} else if(currentMetaTypeId == 'intent'){
				$("#intentValue").val(currentMetaId).trigger('change');
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(4).show();
			} else if(currentMetaTypeId == 'scenario'){
				$('#scenarioValue').val(currentMetaId).trigger('change');
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(5).show();
			} else if(currentMetaTypeId == 'phoneLink'){
				$('#phoneLinkValue').val(currentMetaId);
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(6).show();
			} else if(currentMetaTypeId == 'userDefine'){
				$('#userDefineValue').val(currentMetaId);
				$('#meta-modal .row').children().slice(1).each(function(){
					$(this).hide();
				});
				$('#meta-modal .row').children().eq(7).show();
			} 
		}
		$('#meta-modal').modal('show');
	}
}

function editMetaInfo(metaIdValue, metaTypeValue){
	var metaNameValue = '';
	var metaContValue = '';
	var metaLinkObj;
	
	var metaId = 'meta_' + curSelectedNodeId;
	var metaTypeId = 'metatype_' + curSelectedNodeId;
	var metaNameId = 'metaname_' + curSelectedNodeId;
	var metaContId = 'metacont_' + curSelectedNodeId;	
	
	var metaNameHtml = '';
	metaNameHtml += '<span class="badge ms-auto bg-secondary" style="top:2px;">';
	if(metaTypeValue == 'urlLink'){
		metaLinkObj = refUrlLinkMap.get(metaIdValue);
		metaNameValue = metaLinkObj.linkNm;
		metaContValue = metaLinkObj.linkValue;
		metaNameHtml += 'URL Link';
	} else if(metaTypeValue == 'appLink'){
		metaLinkObj = refAppLinkMap.get(metaIdValue);
		metaNameValue = metaLinkObj.linkNm;
		metaContValue = metaLinkObj.linkValue;
		metaNameHtml += 'APP Link';
	} else if(metaTypeValue == 'intent'){
		metaNameValue = refDialogMap.get(metaIdValue);
		metaNameHtml += '대화';
	} else if(metaTypeValue == 'scenario'){
		metaNameValue = refDialogModelMap.get(metaIdValue);
		metaNameHtml += '시나리오';
	} else if(metaTypeValue == 'phoneLink'){
		metaNameValue = '커스텀 데이터';
		metaContValue = metaIdValue;
		metaNameHtml += '전화연결';
	} else if(metaTypeValue == 'userDefine'){
		metaNameValue = '커스텀 데이터';
		metaContValue = metaIdValue;
		metaNameHtml += '사용자정의';
	} 
	metaNameHtml += '</span>';
	
	metaNameHtml = metaNameValue + metaNameHtml;
	$('#'+metaId).val(metaIdValue);
	$('#'+metaTypeId).val(metaTypeValue);
	$('#'+metaNameId).html(metaNameHtml);
	$('#'+metaContId).val(metaContValue);
	
	editor.updateHtml(curSelectedNodeId);
	editor.updateNodeMetaData(curSelectedNodeId, metaTypeValue, metaIdValue);
	console.log('[editMetaInfo] metaTypeValue : ' + metaTypeValue + ', metaIdValue : ' + metaIdValue);
}

/*
노드 버튼 삭제
*/
function deleteBtn(e, btnId, btnOutIdx){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	var outputClassName = 'output_' + btnOutIdx;
	console.log('[deleteBtn] nodeDivId is ' + nodeDivId + ', btnId is ' + btnId + ', outputClassName is ' + outputClassName);
	editor.removeNodeOutputCustom(curSelectedNodeId, outputClassName);
	$('#' + btnId).remove();
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
	editor.removeNodeOutputDataCustom(curSelectedNodeId, btnOutIdx);
}

/*
노드 버튼 삭제
*/
function deleteBtnFromModal(btnOutIdx){
	var nodeDivId = 'node-' + curSelectedNodeId;
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var outputClassName = 'output_' + btnOutIdx;
	console.log('[deleteBtnFromModal] nodeDivId is ' + nodeDivId + ', btnId is ' + btnId + ', outputClassName is ' + outputClassName);
	editor.removeNodeOutputCustom(curSelectedNodeId, outputClassName);
	$('#' + btnId).remove();
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
	editor.removeNodeOutputDataCustom(curSelectedNodeId, btnOutIdx);
}

/**
노드 slot filling 모달 실행
*/
function editSlotBtn(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[editSlotBtn] nodeDivId id is ' + nodeDivId);
	
	// HAVE TO DO
	var nodeObj = editor.getNodeFromId(curSelectedNodeId);
	var slotArr = nodeObj.data.slot;
	
	$('#slot_fields').html('');
	$('#preSlotVariableKey').val('');
	$("#preSlotEntityBtnSrch").val(null).trigger('change');
	$('#preRequiredFlag').prop('checked',false).trigger('change');
	$('#preSlotDefVariableValue').attr("disabled", false);
    $('#preRequestionPlus').attr("disabled", true);
    $('#pre_requestion_fields').html('');
		
	if(typeof slotArr != "undefined" && slotArr != null){
		var slotVariableKey;
		var slotEntityNo;
		var requiredFlag;
		var slotDefVariableValue;
		var requestionArr;
		for(idx in slotArr){
			slotVariableKey = slotArr[idx].variableKey;
			slotEntityNo = slotArr[idx].entityNo;
			requiredFlag = slotArr[idx].requiredFlag;
			slotDefVariableValue = slotArr[idx].defVariableValue;
			requestionArr = slotArr[idx].requestion;
			
			if(idx == 0){
				$('#preSlotVariableKey').val(slotVariableKey);
				$("#preSlotEntityBtnSrch").val(slotEntityNo).trigger('change');
				if(requiredFlag){
					$('#preRequiredFlag').prop('checked',true).trigger('change');
					// 재질의
					$('#pre_requestion_fields').html('');
					for(i in requestionArr){
						pre_requestion_fields(requestionArr[i]);
					}
				} else {
					// default 변수값
					$('#preSlotDefVariableValue').val(slotDefVariableValue);
				}
			} else {
				slot_fields(slotVariableKey, slotDefVariableValue, slotEntityNo, requiredFlag, requestionArr);
			}
		}
	}
	$('#slot-modal').modal('show');
}

/*
노드 컨디션 모달 실행 
*/
function editConditaionBtn(e){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	console.log('[editConditaionBtn] nodeDivId id is ' + nodeDivId);
	$('#regex_fields').html('');
	$('#condition_fields').html('');
	$('#preVariableKey').val('');
	$('#preVariableValue').val('');
	$('#preTagKeywords').tagsinput('removeAll');
	$('#postVariableKey').val('');
	$('#postVariableValue').val('');
	
	var btnKey = '';
	var btnValue = '';
	var btnTag = '';
	var btnRegexp;
	var nodeObj = editor.getNodeFromId(curSelectedNodeId);
	var curBtnObj = nodeObj.data.name;
	var curBtnSortObj = nodeObj.data.sort;
	var curBtnDetailObj = nodeObj.data.detail;
	
	var dataArr = new Array();
	var data;
	
	if(typeof curBtnObj != "undefined" && curBtnObj != null){
		var keys = Object.keys(curBtnObj);
		for(idx in keys) {
			if(typeof curBtnDetailObj != "undefined" && curBtnDetailObj != null){
				if(typeof curBtnDetailObj[keys[idx]] != "undefined" && curBtnDetailObj[keys[idx]] != null){
					btnKey = curBtnDetailObj[keys[idx]].key;
					btnValue = curBtnDetailObj[keys[idx]].value;
					btnTag = curBtnDetailObj[keys[idx]].tag;
					btnRegexp = curBtnDetailObj[keys[idx]].regexpArr;
				}				
			}
			if(keys[idx] == conditionBtnIfIdx){ // if 조건
				$('#preVariableKey').val(btnKey);
				$('#preVariableValue').val(btnValue);
				$('#preTagKeywords').tagsinput('add', btnTag);
				if(btnRegexp != null){
					for(i in btnRegexp) {
						pre_regex_fields(btnRegexp[i]);
					}
				}
			} else if(keys[idx] == conditionBtnElseIdx){ // else 조건
				$('#postVariableKey').val(btnKey);
				$('#postVariableValue').val(btnValue);
			} else {
				data = new Object();
				data.id = keys[idx];
				data.btnKey = btnKey;
				data.btnValue = btnValue;
				data.btnTag = btnTag;
				data.btnRegexp = btnRegexp;
				if(typeof curBtnSortObj == "undefined" || curBtnSortObj == null){
					data.sort = idx;
				} else {
					data.sort = curBtnSortObj[keys[idx]];
				}
				dataArr.push(data);
				console.log('[editConditaionBtn] id : ' + keys[idx] + ', btnKey : ' + btnKey + ', btnValue : ' + btnValue + ', btnTag : ' + btnTag);
			}
		}	
		
		dataArr.sort(function (a, b) { 
			return a.sort < b.sort ? -1 : a.sort > b.sort ? 1 : 0;  
		});
		for(idx in dataArr){
			condition_fields(dataArr[idx].id, dataArr[idx].btnKey, dataArr[idx].btnValue, dataArr[idx].btnTag, dataArr[idx].btnRegexp);
		}
	} 
	
	$('#gateway-modal').modal('show');
}

/*
노드 컨디션 추가
*/
function plusGatewayCondition(variableKey, variableValue, tagKeyword, regexpArr){
	
	var btnOutIdx = 3;
	var className = '';
	var idx = 0;
	$('#btnDiv_'+curSelectedNodeId).children().each(function(){
		className = $(this).attr('class');
		idx = className.substring('button-'.length);
		if(parseInt(idx) >= btnOutIdx){
			btnOutIdx = parseInt(idx) + 1;
		}
	});
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var btnClass = 'button-' + btnOutIdx;
	console.log('[plusGatewayCondition] btnId : ' + btnId + ', btnOutIdx : ' + btnOutIdx);

	var buttonHtml = `
	<div class="${btnClass}" id="${btnId}" style="display:flex;align-items: center;">
		<i class="fas fa-sort fa-lg" style="cursor:pointer;"></i>
			<div class="row justify-content-center" style="padding-left:18px;padding-top:5px;">
				<div>
					<button type="button" class="btn waves-effect waves-light btn-secondary" style="width:150px;" data-toggle="popover" data-trigger="focus" title="ELSE IF 조건" data-btnId="${btnId}" >ELSE IF 조건</button>
				</div>
			</div>
		<i class="fas fa-minus" onclick="deleteCondition(event,'${btnId}','${btnOutIdx}');" style="left:187px;cursor:pointer;"></i>
	</div>
	`;
	$('#btnDiv_'+curSelectedNodeId).append(buttonHtml);
	editor.addNodeOutput(curSelectedNodeId, btnOutIdx);
	var detailDataObj = new Object();
	detailDataObj.key = variableKey;
	detailDataObj.value = variableValue;
	detailDataObj.tag = tagKeyword;
	detailDataObj.regexpArr = regexpArr;
	editor.updateNodeOutputDetail(curSelectedNodeId, btnOutIdx, 'else if', detailDataObj);
}

/*
노드 컨디션 갱신 (ID/버튼라벨/변수명/변수값)
*/
function updateGatewayCondition(id, btnLabel, variableKey, variableValue, tagKeyword, regexpArr){
	var detailDataObj = new Object();
	detailDataObj.key = variableKey;
	detailDataObj.value = variableValue;
	detailDataObj.tag = tagKeyword;
	detailDataObj.regexpArr = regexpArr;
	editor.updateNodeOutputDetail(curSelectedNodeId, id, btnLabel, detailDataObj);
}

/*
api 응답노드 컨디션 갱신 (ID/버튼라벨/변수명/변수값)
*/
function updateApiResCondition(id, btnLabel, detailDataObj) {
	if(id == apiBtnExistsIdx){
		var listDataFlag = detailDataObj.listDataFlag;
		var btnId = 'btn_' + curSelectedNodeId + '_' + id;
		var outputClassName = 'output_' + id;
		
		var nextNodePortFlag = true;
		
		if(listDataFlag){
			if(detailDataObj.listData.dataType == 'view'){
				if(typeof detailDataObj.defLabel == "undefined" || detailDataObj.defLabel == null || detailDataObj.defLabel == ''){
					nextNodePortFlag = false;
				}
			} 
		} else {
			if(typeof detailDataObj.defLabel == "undefined" || detailDataObj.defLabel == null || detailDataObj.defLabel == ''){
				nextNodePortFlag = false;
			}
		}
		
		if(!nextNodePortFlag){
			// 링크 포트 삭제
			if($('#' + btnId + ' .output').hasClass(outputClassName)){
				editor.removeNodeOutputCustom(curSelectedNodeId, outputClassName);				
			}
		} else {
			// 링크 포트 생성
			if(!$('#' + btnId + ' .output').hasClass(outputClassName)){
				var btnOutIdx = id;
				editor.addNodeOutput(curSelectedNodeId, btnOutIdx);
				editor.updateHtml(curSelectedNodeId);
				$('#btnDiv_pre_'+curSelectedNodeId+' .output').css('right','-18px');					
			}
		}
	}
	editor.updateNodeOutputDetail(curSelectedNodeId, id, btnLabel, detailDataObj);
}

/*
노드 컨디션 삭제
*/
function deleteCondition(e,btnId,btnOutIdx){
	const nodeDivId = e.target.closest(".drawflow-node").id;
	var outputClassName = 'output_' + btnOutIdx;
	console.log('[deleteCondition] nodeDivId id is ' + nodeDivId + ', btnId is ' + btnId + ', outputClassName is ' + outputClassName);
	editor.removeNodeOutputCustom(curSelectedNodeId,outputClassName);
	$('#' + btnId).remove();
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
	editor.removeNodeOutputDataCustom(curSelectedNodeId, btnOutIdx);
}

/*
노드 컨디션 삭제
*/
function deleteConditionFromModal(btnOutIdx){
	var nodeDivId = 'node-' + curSelectedNodeId;
	var btnId = 'btn_' + curSelectedNodeId + '_' + btnOutIdx;
	var outputClassName = 'output_' + btnOutIdx;
	console.log('[deleteBtnFromModal] nodeDivId is ' + nodeDivId + ', btnId is ' + btnId + ', outputClassName is ' + outputClassName);
	editor.removeNodeOutputCustom(curSelectedNodeId, outputClassName);
	$('#' + btnId).remove();
	// 해당 노드에 대한 링크 위치 업데이트
	editor.updateConnectionNodes(nodeDivId);
	editor.removeNodeOutputDataCustom(curSelectedNodeId, btnOutIdx);
}

function updateApiReqInfo(apiNo, apiName, paramArr){
	
	var apiId = 'api_' + curSelectedNodeId;
	var apiNameId = 'apiname_' + curSelectedNodeId;
	
	$('#'+apiId).val(apiNo);
	$('#'+apiNameId).html(apiName);
	
	editor.updateHtml(curSelectedNodeId);
	editor.updateNodeApiData(curSelectedNodeId, apiNo, paramArr);
}

function updateSlotInfo(slotArr){
	editor.updateNodeSlotData(curSelectedNodeId, slotArr);
}

function nodeValidationAlert(outputNodeName, inputNodeName, outputClassName){
	if(outputNodeName == 'startNode') {
		if(!(inputNodeName == 'basicNode' || inputNodeName == 'slotNode')){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "시작노드는 기본답변노드와 SLOT노드에만 연결 할 수 있습니다!",
			  });
			return false;
		}
	}

	if(outputNodeName == 'basicNode') {
		if(outputClassName == 'output_0'){
			if(inputNodeName != 'gatewayNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "게이트웨이버튼은 응답게이트웨이노드에만 연결 할 수 있습니다!",
				  });
				return false;
			}
		} else {
			if(inputNodeName == 'gatewayNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "게이트웨이버튼은 응답게이트웨이노드에만 연결 할 수 있습니다!",
				  });
				return false;
			}
		}
		
		if(inputNodeName == 'slotNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "SLOT노드는 시작노드에서만 연결 할 수 있습니다!",
			  });
			return false;
		}
	}
	
	if(outputNodeName == 'slotNode') {
		if(inputNodeName == 'metaNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "SLOT노드는 메타정보노드에 연결 할 수 없습니다!",
			  });
			return false;
		}
		if(outputClassName == 'output_1'){
			if(!(inputNodeName == 'basicNode' || inputNodeName == 'apiNode')){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "슬롯필링 성공의 겨우는 기본답변노드와 API노드에만 연결 할 수 있습니다!",
				  });
				return false;
			}
		} else {
			if(inputNodeName != 'basicNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "슬롯필링 실패 경우는 기본답변노드에만 연결 할 수 있습니다!",
				  });
				return false;
			}
		}
	}

	if(outputNodeName == 'gatewayNode') {
		if(inputNodeName == 'gatewayNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "응답게이트웨이노드는 응답게이트웨이노드에 연결 할 수 없습니다!",
			  });
			return false;
		}
		if(inputNodeName == 'metaNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "응답게이트웨이노드는 메타정보노드에 연결 할 수 없습니다!",
			  });
			return false;
		}
		if(inputNodeName == 'slotNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "SLOT노드는 시작노드에서만 연결 할 수 있습니다!",
			  });
			return false;
		}
	}

	if(outputNodeName == 'apiNode') {
		if(inputNodeName == 'metaNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "API노드는 메타정보노드에 연결 할 수 없습니다! 기본답변노드 연결 후에 진행해주세요",
			  });
			return false;
		}
		if(outputClassName == 'output_2' || outputClassName == 'output_3'){
			if(inputNodeName != 'basicNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "결과가 없을 경우와 예외발생 경우는 기본답변노드에 연결 할 수 있습니다!",
				  });
				return false;
			}
		}
		if(outputClassName == 'output_0'){
			if(inputNodeName != 'gatewayNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "게이트웨이버튼은 응답게이트웨이노드에만 연결 할 수 있습니다!",
				  });
				return false;
			}
		} else {
			if(inputNodeName == 'gatewayNode'){
				Swal.fire({
					type: "error",
					title: "노드 연결 불가",
					text: "게이트웨이버튼을 통해서 응답게이트웨이노드에 연결 할 수 있습니다!",
				  });
				return false;
			}
		}
		if(inputNodeName == 'slotNode'){
			Swal.fire({
				type: "error",
				title: "노드 연결 불가",
				text: "SLOT노드는 시작노드에서만 연결 할 수 있습니다!",
			  });
			return false;
		}
	}

	return true;
}

var transform = '';
function showpopup(e) {
	e.target.closest(".drawflow-node").style.zIndex = "9999";
	e.target.children[0].style.display = "block";
	transform = editor.precanvas.style.transform;
	editor.precanvas.style.transform = '';
	editor.precanvas.style.left = editor.canvas_x +'px';
	editor.precanvas.style.top = editor.canvas_y +'px';

	editor.editor_mode = "fixed";
}

function closemodal(e) {
	e.target.closest(".drawflow-node").style.zIndex = "2";
	e.target.parentElement.parentElement.style.display  ="none";
	editor.precanvas.style.transform = transform;
	editor.precanvas.style.left = '0px';
	editor.precanvas.style.top = '0px';
	editor.editor_mode = "edit";
}

function changeMode(option) {
	if(option == 'lock') {
		lock.style.display = 'none';
		unlock.style.display = 'block';
	} else {
		lock.style.display = 'block';
		unlock.style.display = 'none';
	}
}

function filteringXSS(origin) {
	return origin.replace(/\<|\>|\"|\'|\%|\;|\(|\)|\&|\+|\-/g, "");
}