xquery version "3.0";

declare variable $exist:path external;
declare variable $exist:resource external;
declare variable $exist:controller external;
declare variable $exist:prefix external;
declare variable $exist:root external;

if ($exist:path = ('/', '')) then
    (: forward root path to index.xq :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist"><redirect url="index.xml"/></dispatch>
    else
    (: everything else is passed through :)
    <dispatch xmlns="http://exist.sourceforge.net/NS/exist"><cache-control cache="yes"/></dispatch>
