<#assign props = properties.getCarouselConfiguration("/Contribution Folders/SystemContent/"+lang+"/main_page_main/carousel/", "veMainPageMainCarouselCms_"+lang)>
<script type="text/javascript">
	var mainCarouselDayTabs = [
		<#list 1..props.tabsNo?number as i>
			<#assign arg = props["tab"+i]>
			<#if i != props.tabsNo?number>
			'${cms.getContent("${arg}")}',
			<#else>
			'${cms.getContent("${arg}")}'
			</#if>
		</#list>
	];
	var mainCarouselNightTabs = [
		<#list 1..props.nightTabsNo?number as i>
			<#assign arg = props["nightTab"+i]>
			<#if i != props.nightTabsNo?number>
			'${cms.getContent("${arg}")}',
			<#else>
			'${cms.getContent("${arg}")}'
			</#if>
		</#list>
	];
</script>

<div id="veMainPageMainCarousel" class="jcarousel jcarousel-big"><!-- @author Karol.Chomik@gtech.com -->
	<script type="text/javascript">
		var veMainPageMainCarousel = new web10.VeMainPageMainCarousel('veMainPageMainCarousel');
		web10.CORE.register(veMainPageMainCarousel);
	</script>
</div>