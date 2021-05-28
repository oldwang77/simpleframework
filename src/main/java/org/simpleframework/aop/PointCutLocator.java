package org.simpleframework.aop;

import org.aspectj.weaver.tools.PointcutExpression;
import org.aspectj.weaver.tools.PointcutParser;
import org.aspectj.weaver.tools.ShadowMatch;

import java.lang.reflect.Method;

/**
 * 解析Aspect表达式，并且定位被织入的目标
 */
public class PointCutLocator {
    // pointCut解析器，直接给他附上Aspect所有表达式，以便支持众多表达式解析
    private PointcutParser pointcutParser = PointcutParser.getPointcutParserSupportingSpecifiedPrimitivesAndUsingContextClassloaderForResolution(
            PointcutParser.getAllSupportedPointcutPrimitives()
    );

    // 表达式解析器
    private PointcutExpression pointcutExpression;

    public PointCutLocator(String expression){
        this.pointcutExpression = pointcutParser.parsePointcutExpression(expression);
    }

    /**
     * 判断传入的Class对象是否是Aspect的目标代理类，即匹配PointCut表达式（初筛）
     * @param targetClass
     * @return
     */
    public boolean roughMatch(Class<?> targetClass){
        // couldMatchJoinPointsInType比较坑，只能校验within
        // 不能校验(execution,call,get,set)，面对不能校验的表达式，它会直接返回true
        return pointcutExpression.couldMatchJoinPointsInType(targetClass);
    }

    /**
     * 判断传入的Method对象是否是Aspect的目标代理方法，即匹配PointCut表达式（精筛）
     * @param method
     * @return
     */
    public boolean accurateMatches(Method method){
        ShadowMatch shadowMatch = pointcutExpression.matchesMethodExecution(method);
        // 完全匹配，如果不是完全匹配，返回false
        if(shadowMatch.alwaysMatches()) return true;
        else return false;
    }

}
