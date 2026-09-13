import { NumberFormatStyle } from '../types';

export interface EvaluationResult {
  success: boolean;
  value: number;
  formatted: string;
  error?: string;
}

export class MathEvaluator {
  static evaluate(
    expression: string,
    isDegreeMode: boolean = true,
    precision: number = -1,
    formatStyle: NumberFormatStyle = 'STANDARD'
  ): EvaluationResult {
    if (!expression || expression.trim() === '') {
      return { success: true, value: 0, formatted: '0' };
    }

    try {
      const sanitized = this.sanitizeExpression(expression, formatStyle);
      const tokens = this.tokenize(sanitized);
      const rpn = this.shuntingYard(tokens);
      const value = this.evalRPN(rpn, isDegreeMode);

      if (isNaN(value)) {
        return { success: false, value: 0, formatted: '', error: 'Undefined' };
      }
      if (!isFinite(value)) {
        return { success: false, value: 0, formatted: '', error: 'Division by zero' };
      }

      const formatted = this.formatNumber(value, precision, formatStyle);
      return { success: true, value, formatted };
    } catch (e: any) {
      return { success: false, value: 0, formatted: '', error: e.message || 'Invalid syntax' };
    }
  }

  static evaluatePartial(
    expression: string,
    isDegreeMode: boolean = true,
    precision: number = -1,
    formatStyle: NumberFormatStyle = 'STANDARD'
  ): EvaluationResult {
    if (!expression || expression.trim() === '') {
      return { success: true, value: 0, formatted: '0' };
    }

    const fullResult = this.evaluate(expression, isDegreeMode, precision, formatStyle);
    if (fullResult.success) return fullResult;

    let expr = expression.trim();
    const visited = new Set<string>();

    while (expr.length > 0 && !visited.has(expr)) {
      visited.add(expr);
      expr = expr.slice(0, -1).trim();
      if (!expr) break;

      const res = this.evaluate(expr, isDegreeMode, precision, formatStyle);
      if (res.success) return res;

      // Try auto-closing parentheses
      const openCount = (expr.match(/\(/g) || []).length;
      const closeCount = (expr.match(/\)/g) || []).length;
      if (openCount > closeCount) {
        const autoClosed = expr + ')'.repeat(openCount - closeCount);
        const closedRes = this.evaluate(autoClosed, isDegreeMode, precision, formatStyle);
        if (closedRes.success) return closedRes;
      }
    }

    return fullResult;
  }

  private static sanitizeExpression(expr: string, formatStyle: NumberFormatStyle): string {
    let s = expr
      .replace(/×/g, '*')
      .replace(/÷/g, '/')
      .replace(/−/g, '-')
      .replace(/π/g, 'pi')
      .replace(/E/g, 'e')
      .replace(/\s+/g, '')
      .replace(/\u00A0/g, '');

    if (formatStyle === 'EUROPEAN') {
      s = s.replace(/(?<=\d)\.(?=\d)/g, '');
      s = s.replace(/,/g, '.');
    } else {
      s = s.replace(/,/g, '');
    }

    // Insert implicit multiplication: e.g. 2pi -> 2*pi, 3( -> 3*(, )4 -> )*4
    s = s.replace(/(\d|\)|pi|e)(pi|e|\(|sin|cos|tan|asin|acos|atan|sinh|cosh|tanh|log|ln|sqrt|abs)/g, '$1*$2');
    s = s.replace(/(\)|pi|e)(\d)/g, '$1*$2');
    s = s.replace(/(\))(\()/g, '$1*$2');

    return s;
  }

  private static tokenize(expr: string): string[] {
    const tokens: string[] = [];
    let i = 0;
    const len = expr.length;

    while (i < len) {
      const ch = expr[i];

      if (ch === ' ' || ch === '\t') {
        i++;
        continue;
      }

      // Numbers
      if (/\d/.test(ch) || (ch === '.' && i + 1 < len && /\d/.test(expr[i + 1]))) {
        let numStr = '';
        while (i < len && (/\d/.test(expr[i]) || expr[i] === '.')) {
          numStr += expr[i];
          i++;
        }
        tokens.push(numStr);
        continue;
      }

      // Identifiers (functions or constants)
      if (/[a-zA-Z]/.test(ch)) {
        let ident = '';
        while (i < len && /[a-zA-Z0-9]/.test(expr[i])) {
          ident += expr[i];
          i++;
        }
        tokens.push(ident);
        continue;
      }

      // Negative sign vs subtraction
      if (ch === '-') {
        const prev = tokens[tokens.length - 1];
        if (!prev || prev === '(' || ['+', '-', '*', '/', '^', '%'].includes(prev)) {
          tokens.push('u-');
          i++;
          continue;
        }
      }

      tokens.push(ch);
      i++;
    }

    return tokens;
  }

  private static precedence(op: string): number {
    switch (op) {
      case '+':
      case '-':
        return 1;
      case '*':
      case '/':
      case '%':
        return 2;
      case '^':
        return 3;
      case 'u-':
        return 4;
      default:
        return 0;
    }
  }

  private static isRightAssociative(op: string): boolean {
    return op === '^' || op === 'u-';
  }

  private static isFunction(token: string): boolean {
    return ['sin', 'cos', 'tan', 'asin', 'acos', 'atan', 'sinh', 'cosh', 'tanh', 'log', 'ln', 'sqrt', 'abs', 'fact'].includes(token);
  }

  private static shuntingYard(tokens: string[]): string[] {
    const output: string[] = [];
    const opStack: string[] = [];

    for (const token of tokens) {
      if (!isNaN(Number(token))) {
        output.push(token);
      } else if (token === 'pi') {
        output.push(Math.PI.toString());
      } else if (token === 'e') {
        output.push(Math.E.toString());
      } else if (this.isFunction(token)) {
        opStack.push(token);
      } else if (token === '(') {
        opStack.push(token);
      } else if (token === ')') {
        while (opStack.length > 0 && opStack[opStack.length - 1] !== '(') {
          output.push(opStack.pop()!);
        }
        if (opStack.length > 0 && opStack[opStack.length - 1] === '(') {
          opStack.pop();
        }
        if (opStack.length > 0 && this.isFunction(opStack[opStack.length - 1])) {
          output.push(opStack.pop()!);
        }
      } else if (['+', '-', '*', '/', '^', '%', 'u-'].includes(token)) {
        while (
          opStack.length > 0 &&
          opStack[opStack.length - 1] !== '(' &&
          (this.precedence(opStack[opStack.length - 1]) > this.precedence(token) ||
            (this.precedence(opStack[opStack.length - 1]) === this.precedence(token) && !this.isRightAssociative(token)))
        ) {
          output.push(opStack.pop()!);
        }
        opStack.push(token);
      }
    }

    while (opStack.length > 0) {
      output.push(opStack.pop()!);
    }

    return output;
  }

  private static factorial(n: number): number {
    if (n < 0 || n > 170) return NaN;
    if (Math.floor(n) !== n) return NaN;
    let res = 1;
    for (let i = 2; i <= n; i++) res *= i;
    return res;
  }

  private static evalRPN(rpn: string[], isDegreeMode: boolean): number {
    const stack: number[] = [];

    const toRad = (v: number) => isDegreeMode ? (v * Math.PI) / 180 : v;
    const toDeg = (v: number) => isDegreeMode ? (v * 180) / Math.PI : v;

    for (const token of rpn) {
      if (!isNaN(Number(token))) {
        stack.push(Number(token));
      } else if (token === 'u-') {
        if (stack.length < 1) throw new Error('Invalid syntax');
        stack.push(-stack.pop()!);
      } else if (['+', '-', '*', '/', '^', '%'].includes(token)) {
        if (stack.length < 2) throw new Error('Invalid syntax');
        const b = stack.pop()!;
        const a = stack.pop()!;
        switch (token) {
          case '+': stack.push(a + b); break;
          case '-': stack.push(a - b); break;
          case '*': stack.push(a * b); break;
          case '/': stack.push(b === 0 ? Infinity : a / b); break;
          case '^': stack.push(Math.pow(a, b)); break;
          case '%': stack.push(a * (b / 100)); break;
        }
      } else if (this.isFunction(token)) {
        if (stack.length < 1) throw new Error('Invalid syntax');
        const val = stack.pop()!;
        switch (token) {
          case 'sin': stack.push(Math.sin(toRad(val))); break;
          case 'cos': stack.push(Math.cos(toRad(val))); break;
          case 'tan': stack.push(Math.tan(toRad(val))); break;
          case 'asin': stack.push(toDeg(Math.asin(val))); break;
          case 'acos': stack.push(toDeg(Math.acos(val))); break;
          case 'atan': stack.push(toDeg(Math.atan(val))); break;
          case 'sinh': stack.push(Math.sinh(val)); break;
          case 'cosh': stack.push(Math.cosh(val)); break;
          case 'tanh': stack.push(Math.tanh(val)); break;
          case 'log': stack.push(Math.log10(val)); break;
          case 'ln': stack.push(Math.log(val)); break;
          case 'sqrt': stack.push(Math.sqrt(val)); break;
          case 'abs': stack.push(Math.abs(val)); break;
          case 'fact': stack.push(this.factorial(val)); break;
        }
      }
    }

    if (stack.length !== 1) throw new Error('Invalid syntax');
    return stack[0];
  }

  static formatNumber(val: number, precision: number = -1, formatStyle: NumberFormatStyle = 'STANDARD'): string {
    if (isNaN(val)) return 'Error';
    if (!isFinite(val)) return val > 0 ? 'Infinity' : '-Infinity';

    let str: string;
    if (precision >= 0) {
      str = val.toFixed(precision);
    } else {
      // Clean display of float precision issues like 0.1 + 0.2
      str = parseFloat(val.toPrecision(12)).toString();
    }

    if (formatStyle === 'EUROPEAN') {
      const parts = str.split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, '.');
      return parts.join(',');
    } else if (formatStyle === 'INDIAN') {
      const parts = str.split('.');
      let lastThree = parts[0].substring(parts[0].length - 3);
      const otherNumbers = parts[0].substring(0, parts[0].length - 3);
      if (otherNumbers !== '') {
        lastThree = ',' + lastThree;
      }
      parts[0] = otherNumbers.replace(/\B(?=(\d{2})+(?!\d))/g, ',') + lastThree;
      return parts.join('.');
    } else {
      const parts = str.split('.');
      parts[0] = parts[0].replace(/\B(?=(\d{3})+(?!\d))/g, ',');
      return parts.join('.');
    }
  }
}
