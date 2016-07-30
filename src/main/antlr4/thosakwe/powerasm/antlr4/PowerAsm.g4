grammar PowerAsm;

SL_CMT: '//' ~('\n')* -> channel(HIDDEN);
WS: (' ' | '\n' | '\r' | '\t' | '\r\n') -> skip;

// Symbols
AMPERSAND: '&';
DOLLAR: '$';
CURLY_L: '{';
CURLY_R: '}';
PAREN_L: '(';
PAREN_R: ')';
SEMI: ';';

// Operators
BW_AND: '&';
BW_OR: '|';
BW_XOR: '^';
EQUALS: '=';
GT: '>';
GTE: '>=';
IS: '==';
L_AND: '&&';
L_OR: '||';
LT: '<';
LTE: '<=';
MINUS: '-';
MODULO: '%';
NOT: '!=';
PLUS: '+';
SHL: '<<';
SHR: '>>';
SLASH: '/';
TIMES: '*';

// Keywords
CALL: 'call';
DECL: 'decl';
EXTERN: 'extern';
INCLUDE: 'include';
LOCAL: 'local';
POP: 'pop';
PUSH: 'push';
RETURN: 'return';
SET: 'set';

// Types
I8: 'i8';
I16: 'i16';
I32: 'i32';
I64: 'i64';
U8: 'u8';
U16: 'u16';
U32: 'u32';
U64: 'u64';

// Data
CHAR: '\'' ~'\'' '\'';
HEX: '0x' [A-Fa-f0-9]+;
NUM: MINUS? [0-9]+;
fragment ESCAPED: '\\"' | '\\r' | '\\n';
RAW_STRING: 'r"' (ESCAPED | ~('\n'|'\r'))*? '"';
STRING: '"' (ESCAPED | ~('\n'|'\r'))*? '"';

// This should also be last
ID: [A-Za-z_] [A-Za-z0-9_]*;

compilationUnit: (includeDecl | labelDecl | stmtList)*;
includeDecl: INCLUDE string SEMI?;
string: STRING | RAW_STRING;
labelDecl: name=ID CURLY_L stmtList? CURLY_R;

stmtList: (stmt SEMI?)+;
stmt:
    symbolDeclStmt
    | localSymbolDeclStmt
    | setStmt
    | pushStmt
    | externCallStmt
    | callStmt
    | returnStmt
;

symbolDeclStmt: DECL type ID expr?;
setStmt: SET ID expr;
localSymbolDeclStmt: LOCAL type ID expr?;
pushStmt: PUSH type expr;
externCallStmt: EXTERN CALL ID;
callStmt: CALL ID;
returnStmt: RETURN expr?;

type:
    I8 #SignedByteType
    | I16 #SignedWordType
    | I32 #SignedDoubleWordType
    | I64 #SignedQuadrupleWordType
    | U8 #UnsignedByteType
    | U16 #UnsignedWordType
    | U32 #UnsignedDoubleWordType
    | U64 #UnsignedQuadrupleWordType
    | type TIMES #PointerType
;

binaryOperator: 
    TIMES
    | SLASH
    | MODULO
    | PLUS
    | MINUS
    | SHL
    | SHR
    | LT
    | LTE
    | GT
    | GTE
    | IS
    | NOT
    | BW_AND
    | BW_XOR
    | BW_OR
    | L_AND
    | L_OR;

expr:
    ID #IdentifierExpr
    | CHAR #CharacterExpr
    | NUM #NumExpr
    | HEX #HexExpr
    | type DOLLAR ID #RegisterExpr
    | AMPERSAND expr #AddressExpr
    | TIMES expr #DeferenceExpr
    | POP type #PopExpr
    | left=expr binaryOperator right=expr #BinaryExpr
    | PAREN_L expr PAREN_R #NestedExpr
;