mkdir -p src/main/webapp
cat > src/main/webapp/jstl-test.jsp <<'EOF'
<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<c:set var="name" value="皇上"/>
JSTL OK：<c:out value="${name}"/>
EOF
