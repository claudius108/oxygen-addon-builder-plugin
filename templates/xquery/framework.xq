xquery version "3.0";

import module "http://expath.org/ns/user-agent";

declare namespace ua = "http://expath.org/ns/user-agent";
declare namespace oxy = "http://oxygenxml.com/extensions/author";

declare variable $ua:document as element() external;

()
