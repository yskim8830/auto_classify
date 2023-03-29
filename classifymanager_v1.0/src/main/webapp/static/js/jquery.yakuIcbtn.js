/**
* 웹서비스 제작시 가장 널리 쓰이는 이미지, 클래스를 클릭 및 오버 하였을 경우
* 지정된 레이어 변경 및,  over Prefix 를 지정한 이미지나, 클래스를 표시하여 준다.
*
* @class
*	name : icbtn , imagebtn, classbtn
*
* @example
*
*   $(".ib").imagebtn({overSuffix : "on"});
*	var layer = ["M_board_output_1","M_board_output_2"];
*	$(".ibtn").imagebtn({overSuffix : "_on", clicked : true , layers : layer});
*	$(".me").classbtn({overSuffix : "_on"});
*	$(".mc").classbtn({overSuffix : "_on", clicked : true});
*	$(".ti").imagebtn({overSuffix : "_on", clicked : true, displayTarget : 'cTarget'});
*
* @demo
*
* @author yaku <yakuyaku@gmail.com>
* @since 2010-05-27
* @version 1.0
*/

(function($){

    $.fn.imagebtn = function(opts){
        opts.overType = "img";
        return $.fn.yakuIcbtn(this,opts);
    };

    $.fn.classbtn = function(opts){
        opts.overType = "class";
        return $.fn.yakuIcbtn(this,opts);
    };

    $.fn.yakuIcbtn = function(els, opts) {

        var elements = [], selectedIndex = -1,
            o = $.extend({}, $.fn.yakuIcbtn.defaults, opts);

        return $(els).each(function(i) {
            var source = this.src, $this  = $(this), overSource = null,
                oLayer  = (o.layers[i]) ? o.layers[i] : null;

            if(o.overType == "class" && o.clsName ) {
                source = getClassName(o.clsName);
            }

            overSource = getOverSource(source);

            elements[i] = {
                original : this ,
                source   : source ,
                overSource : overSource ,
                oLayer   : oLayer ,
                defaults : false
            };

            if($this.hasClass(o.selectClassName) || $this.hasClass(o.defaultClassName)) {
                setOverSource( $this, source, overSource);
                if($this.hasClass(o.selectClassName)){
					elements[i].defaults = true;
				}
				selectedIndex = i;
            };

            if(o.clicked) {
                $this.bind("click.btn", function(event){
                    event.preventDefault();
                    elementOver(i);
                });
            } else {
                $this.bind("mouseenter.btn", function(event){
                    event.preventDefault();
                    elementOver(i);
                });

                $this.bind("mouseleave.btn", function(event){
                    event.preventDefault();
                    elementOut(i);
                });
            }

            function elementOver(index) {
                elementUnSelect(index);
                elementSelect(index);
            }

            function elementOut(index) {
                elementUnSelect(-1);
            }

            function elementSelect(index) {

                var el = elements[index];
                setOverSource($(el.original), el.source, el.overSource);
                if(el.oLayer) {
                    $("#" +  el.oLayer).show();
                }
                selectedIndex = index;
            }

            function elementUnSelect(currentIndex) {
                if( selectedIndex > -1 && selectedIndex != currentIndex ) {
                    var el = elements[selectedIndex];
                    if( el.defaults == false) {
                        setOverSource($(el.original), el.overSource, el.source );
                    }
                    if(el.oLayer) {
                        $("#" + el.oLayer).hide();
                    }
                    selectedIndex = -1;
                }
            }

            function getOverSource(source) {
                var overSource  = "";
                var pattern  = RegExp(".+("+ o.overSuffix + ")","g");

                if(pattern.test(source)) {
                    overSource = source.replace(o.overSuffix,"");
                } else {
                    if(o.overType == "class" ) {
                        overSource = source + o.overSuffix;
                    } else {
                        overSource = source.replace(RegExp("(\.[^\.]+)$"), o.overSuffix + "$1");
                    }
                }
                return overSource;
            }

            function getClassName(name) {
                var cls =  $this.attr("class").split(" ");
                if(cls.length > 1 ) {
                    for( var i in cls ) {
                        if( cls[i] == name ) {
                            return cls[i];
                        }
                    }
                } else {
                    return $this.attr("class");
                }
            }

            function setOverSource($this, source, oSource)
            {
                if(o.displayTarget) {
                    var $tmp =  $this.clone();
                    setAttr($tmp, source, oSource);
                    $("#" + o.displayTarget ).html($tmp);
                }else {
                    setAttr($this, source, oSource);
                }

                function setAttr($obj, soc, oce) {
                    if(o.overType == "class" ) {
                        $obj.removeClass(soc);
                        $obj.addClass(oce);
                    } else {
                        $obj.attr("src",oce);
                    }
                }
            }
        });
    };

    $.fn.yakuIcbtn.defaults = {
        layers : [] ,
        overSuffix : "_on" ,
        clsName    : "classOver" ,
        selectClassName : "icSelected",
        defaultClassName: "icDefault" ,
		addClassName    : "icClsss" ,
        displayTarget   :  null ,
        isOnly   : true  ,
        layerOut : false ,
        clicked  : false
    };

})(jQuery);

