/*
 * JSP generated by Resin-3.1.12 (built Mon, 29 Aug 2011 03:22:08 PDT)
 */

package _jsp;
import javax.servlet.*;
import javax.servlet.jsp.*;
import javax.servlet.http.*;

public class _parameter$list__jsp extends com.caucho.jsp.JavaPage
{
  private static final java.util.HashMap<String,java.lang.reflect.Method> _jsp_functionMap = new java.util.HashMap<String,java.lang.reflect.Method>();
  private boolean _caucho_isDead;
  
  public void
  _jspService(javax.servlet.http.HttpServletRequest request,
              javax.servlet.http.HttpServletResponse response)
    throws java.io.IOException, javax.servlet.ServletException
  {
    javax.servlet.http.HttpSession session = request.getSession(true);
    com.caucho.server.webapp.WebApp _jsp_application = _caucho_getApplication();
    javax.servlet.ServletContext application = _jsp_application;
    com.caucho.jsp.PageContextImpl pageContext = _jsp_application.getJspApplicationContext().allocatePageContext(this, _jsp_application, request, response, null, session, 8192, true, false);
    javax.servlet.jsp.PageContext _jsp_parentContext = pageContext;
    javax.servlet.jsp.JspWriter out = pageContext.getOut();
    final javax.el.ELContext _jsp_env = pageContext.getELContext();
    javax.servlet.ServletConfig config = getServletConfig();
    javax.servlet.Servlet page = this;
    response.setContentType("text/html; charset=UTF-8");
    request.setCharacterEncoding("UTF-8");
    com.caucho.jsp.IteratorLoopSupportTag _jsp_loop_0 = null;
    try {
      out.write(_jsp_string0, 0, _jsp_string0.length);
      if (_jsp_loop_0 == null)
        _jsp_loop_0 = new com.caucho.jsp.IteratorLoopSupportTag();
      java.lang.Object _jsp_items_1 = _caucho_expr_0.evalObject(_jsp_env);
      java.util.Iterator _jsp_iter_1 = com.caucho.jstl.rt.CoreForEachTag.getIterator(_jsp_items_1);
      _jsp_loop_0.init(0, Integer.MAX_VALUE, 1);
      Object _jsp_oldVar_1 = pageContext.getAttribute("item");
      while (_jsp_iter_1.hasNext()) {
        Object _jsp_i_1 = _jsp_iter_1.next();
        pageContext.setAttribute("item", _jsp_i_1);
        _jsp_loop_0.setCurrent(_jsp_i_1, _jsp_iter_1.hasNext());
        out.write(_jsp_string1, 0, _jsp_string1.length);
        if (_caucho_expr_1.evalBoolean(_jsp_env)) {
          out.write(_jsp_string2, 0, _jsp_string2.length);
          _caucho_expr_2.print(out, _jsp_env, false);
          out.write(_jsp_string3, 0, _jsp_string3.length);
          _caucho_expr_3.print(out, _jsp_env, false);
          out.write(_jsp_string4, 0, _jsp_string4.length);
          _caucho_expr_4.print(out, _jsp_env, false);
          out.write(_jsp_string5, 0, _jsp_string5.length);
          _caucho_expr_5.print(out, _jsp_env, false);
          out.write(_jsp_string6, 0, _jsp_string6.length);
          _caucho_expr_6.print(out, _jsp_env, false);
          out.write(_jsp_string7, 0, _jsp_string7.length);
        }
        out.write(_jsp_string8, 0, _jsp_string8.length);
      }
      pageContext.pageSetOrRemove("item", _jsp_oldVar_1);
      out.write(_jsp_string9, 0, _jsp_string9.length);
    } catch (java.lang.Throwable _jsp_e) {
      pageContext.handlePageException(_jsp_e);
    } finally {
      _jsp_application.getJspApplicationContext().freePageContext(pageContext);
    }
  }

  private java.util.ArrayList _caucho_depends = new java.util.ArrayList();

  public java.util.ArrayList _caucho_getDependList()
  {
    return _caucho_depends;
  }

  public void _caucho_addDepend(com.caucho.vfs.PersistentDependency depend)
  {
    super._caucho_addDepend(depend);
    com.caucho.jsp.JavaPage.addDepend(_caucho_depends, depend);
  }

  public boolean _caucho_isModified()
  {
    if (_caucho_isDead)
      return true;
    if (com.caucho.server.util.CauchoSystem.getVersionId() != 7170261747151080670L)
      return true;
    for (int i = _caucho_depends.size() - 1; i >= 0; i--) {
      com.caucho.vfs.Dependency depend;
      depend = (com.caucho.vfs.Dependency) _caucho_depends.get(i);
      if (depend.isModified())
        return true;
    }
    return false;
  }

  public long _caucho_lastModified()
  {
    return 0;
  }

  public java.util.HashMap<String,java.lang.reflect.Method> _caucho_getFunctionMap()
  {
    return _jsp_functionMap;
  }

  public void init(ServletConfig config)
    throws ServletException
  {
    com.caucho.server.webapp.WebApp webApp
      = (com.caucho.server.webapp.WebApp) config.getServletContext();
    super.init(config);
    com.caucho.jsp.TaglibManager manager = webApp.getJspApplicationContext().getTaglibManager();
    manager.addTaglibFunctions(_jsp_functionMap, "c", "http://java.sun.com/jsp/jstl/core");
    com.caucho.jsp.PageContextImpl pageContext = new com.caucho.jsp.PageContextImpl(webApp, this);
    _caucho_expr_0 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${result}");
    _caucho_expr_1 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${not empty item.value}");
    _caucho_expr_2 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${item.persistantId}");
    _caucho_expr_3 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${item.type}");
    _caucho_expr_4 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${item.name}");
    _caucho_expr_5 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${item.usedCount}");
    _caucho_expr_6 = com.caucho.jsp.JspUtil.createExpr(pageContext.getELContext(), "${item.value}");
  }

  public void destroy()
  {
      _caucho_isDead = true;
      super.destroy();
  }

  public void init(com.caucho.vfs.Path appDir)
    throws javax.servlet.ServletException
  {
    com.caucho.vfs.Path resinHome = com.caucho.server.util.CauchoSystem.getResinHome();
    com.caucho.vfs.MergePath mergePath = new com.caucho.vfs.MergePath();
    mergePath.addMergePath(appDir);
    mergePath.addMergePath(resinHome);
    com.caucho.loader.DynamicClassLoader loader;
    loader = (com.caucho.loader.DynamicClassLoader) getClass().getClassLoader();
    String resourcePath = loader.getResourcePathSpecificFirst();
    mergePath.addClassPath(resourcePath);
    com.caucho.vfs.Depend depend;
    depend = new com.caucho.vfs.Depend(appDir.lookup("Parameter$List.jsp"), 7680936076688395928L, false);
    com.caucho.jsp.JavaPage.addDepend(_caucho_depends, depend);
  }

  static {
    try {
    } catch (Exception e) {
      e.printStackTrace();
      throw new RuntimeException(e);
    }
  }
  private static com.caucho.el.Expr _caucho_expr_0;
  private static com.caucho.el.Expr _caucho_expr_1;
  private static com.caucho.el.Expr _caucho_expr_2;
  private static com.caucho.el.Expr _caucho_expr_3;
  private static com.caucho.el.Expr _caucho_expr_4;
  private static com.caucho.el.Expr _caucho_expr_5;
  private static com.caucho.el.Expr _caucho_expr_6;

  private final static char []_jsp_string6;
  private final static char []_jsp_string4;
  private final static char []_jsp_string8;
  private final static char []_jsp_string1;
  private final static char []_jsp_string9;
  private final static char []_jsp_string7;
  private final static char []_jsp_string3;
  private final static char []_jsp_string2;
  private final static char []_jsp_string5;
  private final static char []_jsp_string0;
  static {
    _jsp_string6 = " times\">".toCharArray();
    _jsp_string4 = "\" parameterName=\"".toCharArray();
    _jsp_string8 = "\r\n\r\n		".toCharArray();
    _jsp_string1 = "\r\n		\r\n			".toCharArray();
    _jsp_string9 = "\r\n	</div>\r\n\r\n\r\n".toCharArray();
    _jsp_string7 = "</a><br/>\r\n			".toCharArray();
    _jsp_string3 = "\" targetelement=\"#log\" parameterType=\"".toCharArray();
    _jsp_string2 = "\r\n			<a href=\"#\" class=\"delete\" itemId=\"".toCharArray();
    _jsp_string5 = "\">[x]</a>\r\n\r\n\r\n<a href=\"#\" class=\"parameterItem\" title=\"used ".toCharArray();
    _jsp_string0 = "\r\n\r\n\r\n<style>\r\n.message {\r\n	font-size: 20px;\r\n}\r\n\r\n.paramlist {\r\n	font-size: 20px;\r\n	text-align: left;\r\n	overflow-x: hidden;\r\n}\r\n</style>\r\n\r\n<script type=\"text/javascript\">\r\n	$(function() {\r\n		\r\n		\r\n		$(\".parameterItem\").click(function() {			\r\n		\r\n			var currentTargetId= $(\"#parameters\").attr(\"targetInputId\");\r\n			console.log(currentTargetId)	;\r\n			$(\"#\"+currentTargetId).val($(this).html());\r\n			\r\n			$(\"#parameters\").toggle();\r\n			\r\n		});\r\n\r\n		$(\".delete\").click(function() {			\r\n			\r\n			//alert(\"want to delete something?\");\r\n			var targetElement=$(this).attr(\"targetelement\");\r\n			var itemId=$(this).attr(\"itemId\");\r\n			var url=\"removeParameter/\"+itemId+\"/\";\r\n			fillResult(url,targetElement);\r\n\r\n			var parameterType=$(this).attr(\"parameterType\");\r\n			var parameterName=$(this).attr(\"parameterName\");\r\n		    \r\n		    	fillResult(\"suggestParameter/\"+parameterType+\"/\"+parameterName+\"/\",\"#parameters\");\r\n	\r\n\r\n		});\r\n		\r\n\r\n		//\r\n\r\n	});\r\n	\r\n	$(document).ready(function() {\r\n\r\n		\r\n		\r\n	\r\n	});\r\n	\r\n	\r\n</script>\r\n\r\nParameter from Previous Input <div id=\"log\"> </div>\r\n	<hr />\r\n\r\n	<div class=\"paramlist\">\r\n		".toCharArray();
  }
}
