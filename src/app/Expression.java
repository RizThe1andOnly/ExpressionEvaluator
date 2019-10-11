package app;

import java.io.*;
import java.util.*;
import java.util.regex.*;

import structures.Stack;

public class Expression {

	public static String delims = " \t*+-/()[]";
			
    /**
     * Populates the vars list with simple variables, and arrays lists with arrays
     * in the expression. For every variable (simple or array), a SINGLE instance is created 
     * and stored, even if it appears more than once in the expression.
     * At this time, values for all variables and all array items are set to
     * zero - they will be loaded from a file in the loadVariableValues method.
     * 
     * @param expr The expression
     * @param vars The variables array list - already created by the caller
     * @param arrays The arrays array list - already created by the caller
     */
    public static void 
    makeVariableLists(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	/** DO NOT create new vars and arrays - they are already created before being sent in
    	 ** to this method - you just need to fill them in.
    	 **/
    	   	
    	
    	String delim2 = "[\\[\\] \t*+-/()]+";
    	String[] setOfVariablesOrArrays = expr.split(delim2,2);
    	
    	char nxtChar = ' ';
    	String varName = setOfVariablesOrArrays[0];
    	int indexOfCurrentVar = expr.indexOf(varName);
    	int indexOfNextChar = indexOfCurrentVar + varName.length();
    	if(indexOfNextChar<expr.length()) nxtChar = expr.charAt(indexOfNextChar);
    	
    	
    	if(nxtChar == '[')
    	{
    		Array arry = new Array(varName);
    		boolean dupeChk = dupeCheck(arry,arrays);
    		if(dupeChk==true) 
    		{	
    			arrays.add(arry);
    		}
    	}
    	else if((varName.equals("")==false)&&varName.charAt(0)!='0'&& varName.charAt(0)!='1'&& 
     		   varName.charAt(0)!='2'&&varName.charAt(0)!='3'&& varName.charAt(0)!='4'&& 
     		   varName.charAt(0)!='5'&&varName.charAt(0)!='6'&& varName.charAt(0)!='7'&& 
     		   varName.charAt(0)!='8'&&	varName.charAt(0)!='9')
    	{
    		Variable var  = new Variable(varName);
    		boolean dupe = dupeCheck(var,vars);
    		if(dupe==true)
    		{	
    			vars.add(var);
    		}
 		}
    	
    	
    	if(setOfVariablesOrArrays.length>1) 
			makeVariableLists(setOfVariablesOrArrays[1],vars,arrays);
   	
    }
    
    //checks to see if there are duplicate variable/array names from above method
    //there will be two methods, one for the vars and one for arrays
    //will return boolean based on if duplicate exists or not
    private static boolean dupeCheck(Variable var, ArrayList<Variable> vars)
    {
    	for(int i=0;i<vars.size();i++)
    	{
    		if(var.equals(vars.get(i))==true) return false;
    	}
    	
    	return true;
    }
    
    private static boolean dupeCheck(Array arry, ArrayList<Array> arrays)
    {
    	for(int i=0;i<arrays.size();i++)
    	{
    		if(arry.equals(arrays.get(i))==true) return false;
    	}
    	
    	return true;
    }
    
    
    
    /**
     * Loads values for variables and arrays in the expression
     * 
     * @param sc Scanner for values input
     * @throws IOException If there is a problem with the input 
     * @param vars The variables array list, previously populated by makeVariableLists
     * @param arrays The arrays array list - previously populated by makeVariableLists
     */
    public static void 
    loadVariableValues(Scanner sc, ArrayList<Variable> vars, ArrayList<Array> arrays) 
    throws IOException {
        while (sc.hasNextLine()) {
            StringTokenizer st = new StringTokenizer(sc.nextLine().trim());
            int numTokens = st.countTokens();
            String tok = st.nextToken();
            Variable var = new Variable(tok);
            Array arr = new Array(tok);
            int vari = vars.indexOf(var);
            int arri = arrays.indexOf(arr);
            if (vari == -1 && arri == -1) {
            	continue;
            }
            int num = Integer.parseInt(st.nextToken());
            if (numTokens == 2) { // scalar symbol
                vars.get(vari).value = num;
            } else { // array symbol
            	arr = arrays.get(arri);
            	arr.values = new int[num];
                // following are (index,val) pairs
                while (st.hasMoreTokens()) {
                    tok = st.nextToken();
                    StringTokenizer stt = new StringTokenizer(tok," (,)");
                    int index = Integer.parseInt(stt.nextToken());
                    int val = Integer.parseInt(stt.nextToken());
                    arr.values[index] = val;              
                }
            }
        }
      
    }
    
    
    
    /**
     * Evaluates the expression.
     * 
     * @param vars The variables array list, with values for all variables in the expression
     * @param arrays The arrays array list, with values for all array items
     * @return Result of evaluation
     */
    public static float 
    evaluate(String expr, ArrayList<Variable> vars, ArrayList<Array> arrays) {
    	/** COMPLETE THIS METHOD **/
    	// following line just a placeholder for compilation
    	Stack<Float> nums = new Stack<Float>();
    	Stack<Character> operators = new Stack<Character>();
    	expr = emptyStringRemoval(expr);
    	return seval(expr,vars,arrays,nums,operators);
    	
    }
    
    
    //additional func to carry out recursive evaluation; need this cuz...
    //...need parameters to carry the stacks for operations
    private static float seval(String expr,ArrayList<Variable> vars, ArrayList<Array> arrays,
    							Stack<Float> nums, Stack<Character> operator)
    {
    	
    	String[] setOfVars = null;
    	String delim2 = "[\\[\\] \t*+-/()]";
		setOfVars = expr.split(delim2,2);
		char nxtChar = ' ';
    	String varName = setOfVars[0];
    	int indexOfCurrentVar = expr.indexOf(varName);
    	int indexOfNextChar = indexOfCurrentVar + varName.length();
    	int end = indexOfNextChar + 1;
		
		
    	if(setOfVars[0].equals("") && (!expr.equals("")))
    	{
    		if(expr.charAt(0)=='(')
    		{
    			int exprIndex = sets(expr,'(',vars,arrays,nums,operator,end,0,varName);
        		if(end<expr.length()) {return seval(expr.substring(exprIndex+1,expr.length()),vars,arrays,nums,operator);}
    		}
    		
       		if((expr.charAt(0)!=')') && (expr.charAt(0)!=']'))
    		{
    			operator.push(expr.charAt(0));
        	    return seval(expr.substring(1,expr.length()),vars,arrays,nums,operator);
    		}
    	}
    	else if(setOfVars[0].equals("")&&(expr.equals("")))
    	{
    		multiplier(nums,operator);
    		addr(nums,operator);
    		return nums.peek();
    	}
    	
    	if(indexOfNextChar<expr.length()) 
    		nxtChar = expr.charAt(indexOfNextChar);
    	else
    		nxtChar = 'x';
    	
    	float currentTerm = 0;
    	
        	
    	if(nxtChar == '[')
    	{
    		int exprIndex = sets(expr,nxtChar,vars,arrays,nums,operator,end,currentTerm,varName);
    		if(end<expr.length()) {return seval(expr.substring(exprIndex+1,expr.length()),vars,arrays,nums,operator);}
       	}
    	
    	
    	if((nxtChar=='+')||(nxtChar=='-')||(nxtChar=='*')||(nxtChar=='/')||(nxtChar=='x'))
    	{
    	    currentTerm = idVar(varName,vars);
    		nums.push(currentTerm);
    		if(nxtChar == '+' || nxtChar == '-' || nxtChar == 'x')
    		{
    			if(!operator.isEmpty())
    			{
    				multiplier(nums,operator);
    			}
   
    			if(nxtChar!='x') 
    			{
    				operator.push(nxtChar);
    				if(end<expr.length()) {return seval(setOfVars[1],vars,arrays,nums,operator);}
    			}
    			else
    			{
    				addr(nums,operator);
    			}
       		}
    		
    	   if(nxtChar== '*' || nxtChar== '/')
    	   {
    		  operator.push(nxtChar); 
    		  if(end<expr.length()) {return  seval(setOfVars[1],vars,arrays,nums,operator);}
    	   }
    		
    	}
    	
    	
    	if((nxtChar == ')'||nxtChar == ']') && (!varName.equals("")))
    	{
    		currentTerm = idVar(varName,vars);
    		nums.push(currentTerm);
    		multiplier(nums,operator);
    		addr(nums,operator);
    		if(end<expr.length()) 
    		{	return seval(expr.substring(end,expr.length()),vars,arrays,nums,operator);}
    	}
    	
    	
    	if((nxtChar==')'||nxtChar==']') && (varName.equals("")))
    	{
    		multiplier(nums,operator);
    		addr(nums,operator);
    		return seval(setOfVars[1],vars,arrays,nums,operator);
    	}
    	
  
    	multiplier(nums,operator);
    	addr(nums,operator);
    	
    	    	
    	return nums.peek();
    	
    }
    
    
    
    //identifies the passed variable or string number with its proper value
    private static int idVar(String varName, ArrayList<Variable> vars)
    {
    	for(int i=0;i<vars.size();i++)
    	{
    		if(varName.equals(vars.get(i).name)) return vars.get(i).value;
    	}
    	
    	return Integer.parseInt(varName);
    }
    
    
    //ids the array and its index and then returns the value there
    private static int idArr(String varName, ArrayList<Array> arrays,int index)
    {
    	Array temp = null;
    	for(int i=0;i<arrays.size();i++)
    	{
    		if(varName.equals(arrays.get(i).name)) temp = arrays.get(i);
    	}
    	return temp.values[index];
    }
    
    //method to just take care of all the adding at end
    private static void addr(Stack<Float>nums,Stack<Character> operator)
    {
    	
    	while((!operator.isEmpty())&&((operator.peek()!='[')||operator.peek()!='('))
    	{
    		float num1 = 0;
    		float num2 = 0;
    		char operate = operator.peek();
    		
    		if(operate == '+')
    		{
    			operate = operator.pop();
    			num1 = nums.pop();
    			num2 = nums.pop();
    			if(!operator.isEmpty())
   				{
   					char chkOperation = operator.peek();
   					if(chkOperation == '-')
   					{   
   						num2 = (-1) * num2;
    					operator.pop();
    					operator.push('+');
    				}
    			}
   				nums.push(num1+num2);
   			}
   			else if(operate == '-')   			
   			{
   				operate = operator.pop();
    			num1 = nums.pop();
    			num2 = nums.pop();
    			num1 = (-1) * num1;
    			if(!operator.isEmpty())
    			{
   					char chkOperation = operator.peek();
   					if(chkOperation == '-')
   					{
   						num2 = (-1) * num2;
   						operator.pop();
    					operator.push('+');
    				}
    			}
    			
    			nums.push(num1+num2);
    				
    		}
    		if(operator.isEmpty()) break;
    		if(operator.peek()=='(' || operator.peek() == '[')
    		{
    			 operator.pop();
    			 break;
    		}
    		if(operator.peek()==' ')
    		{
    			operator.pop();
    		}
    		
    	}
    
    	
    }
    
    //method to do the multiplication/divisions:
    private static void multiplier (Stack<Float>nums,Stack<Character>operator)
    {
    	
    		while((!operator.isEmpty())&&((operator.peek()!='[')||operator.peek()!='('))
    		{
    			float num1 = 0;
    			float num2 = 0;
    			char oprt = operator.peek();
    			
    			if(oprt=='*')
    			{
    				oprt = operator.pop();
    				num1 = nums.pop();
    				num2 = nums.pop();
    				if(!operator.isEmpty())
        			{
        				char chkOperation = operator.peek();
        				if(chkOperation == '/')
        				{
        					num2 = 1 / num2;
        					operator.pop();
        					operator.push('*');
        				}
        			}
    				nums.push(num2*num1);
    			}
    			else if(oprt=='/')
    			{
    				oprt = operator.pop();
    				num1 = nums.pop();
    				num2 = nums.pop();
    				num1 = 1/num1;
    				nums.push(num2*num1);
    			}
    			else if(oprt == '(' || oprt == '[')
    			{
    				break;
    			}
    			else
    				break;
    		}
    		
    		
    	  }
    
    //for ( or [ so parenthesis or arrays:
    private static int sets(String expr, char nxtChar, ArrayList<Variable>vars,
    						 ArrayList<Array> arrays,Stack<Float>nums,Stack<Character>operator,
    						 int end, float currentTerm,String varName)
    {
    	Stack<Float> nums2 = new Stack<Float>();
		Stack<Character> ops2 = new Stack<Character>();
		ops2.push(nxtChar);
		int exprIndex = 0;
		int tracker = 0;
		for(exprIndex=0;exprIndex<expr.length();exprIndex++)
		{
			char p = expr.charAt(exprIndex);
			if(p=='[' || p=='(') tracker++;
			if(p==']'||p==')') tracker--;
			if((p==']'||p==')')&&tracker==0)break;
		}
		
		if(nxtChar=='[')
		{
			int index = (int) seval(expr.substring(end,exprIndex+1),vars,arrays,nums2,ops2);
			currentTerm = idArr(varName,arrays,index);
			nums.push(currentTerm);
		}
		
		if(nxtChar=='(')
		{
			nums.push(seval(expr.substring(end,exprIndex+1),vars,arrays,nums2,ops2));
		}
		
		return exprIndex;
    }
  
    
    private static String emptyStringRemoval(String expr)
    {
    	String temp = "";
    	for(int i=0;i<expr.length();i++)
    	{
    		if(expr.charAt(i) != ' ')
    			temp = temp + expr.charAt(i);
    	}
    	
    	return temp;
    }
    
}
