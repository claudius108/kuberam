xquery version "3.0";

import module "http://expath.org/ns/http-client";
declare option exist:serialize "method=html5 media-type=text/html encoding=utf-8 indent=yes";

declare variable $redmine-server-url := "http://" || request:get-parameter("redmine-server-url", "");

declare function local:display-issue($issue as node()) as node() {
    let $id := $issue/*[local-name() = 'id']/text()
    let $class := lower-case($issue/*[local-name() = 'tracker']/@name)
    
    return
        <div class="post-it {$class} {replace(lower-case($issue/*[local-name() = 'status']/@name), ' ', '-')} priority{$issue/*[local-name() = 'priority']/@id}">
            <a href="{$redmine-server-url}/issues/{$id}" target="_blank">
                <span class="estimate">{$issue/*[local-name() = 'custom_fields']/*[local-name() = 'custom_field' and @name = 'Estimate']/text()}</span>
                <span class="issue_id">{concat('#', $id)}</span>
                <br/>
                <span class="subject">{$issue/*[local-name() = 'subject']/text()}</span>
                <span class="assigned_to">{data($issue/*[local-name() = 'assigned_to']/@name)}</span>
            </a>
        </div>    
};

let $query-string :=  "/issues.xml?project_id=" || request:get-parameter("project", "") || "&amp;fixed_version=" || request:get-parameter("version", "") || "&amp;status_id=*&amp;limit=100" || "&amp;key=" || request:get-parameter("key", "")
let $query-url := $redmine-server-url || $query-string
let $version := request:get-parameter("version", "")
let $issues := <issues xmlns="http://kuberam.ro/ns/redmine-scrum-board">{http:send-request(<http:request method="GET" href="{$query-url}" />)[2]/*/*}</issues>
let $issues-filtered-by-version := $issues/*[./*[local-name() = 'fixed_version' and @name = $version]]
let $project-title := data($issues-filtered-by-version/*[1]/*[local-name() = 'project']/@name)
let $user-stories :=
    for $user-story in $issues-filtered-by-version[./*[local-name() = 'tracker' and lower-case(@name) = 'user story']]
    order by $user-story/*[local-name() = 'subject']
    return $user-story
        

return
    <html xmlns="http://www.w3.org/1999/xhtml">
        <head>
            <title>{$project-title}</title>
            <link href="redmine-scrum-board.css" rel="stylesheet" type="text/css"/>
            <link href='http://fonts.googleapis.com/css?family=Lemon' rel='stylesheet' type='text/css'/>
            <script src="http://ajax.googleapis.com/ajax/libs/jquery/1.8.1/jquery.min.js">/**/</script>
            <script src="redmine-scrum-board.js">/**/</script>            
        </head>
        <body>
            <h2 id="project-title">{$project-title}</h2>
            <div class="issues">
            <table>
                <tr>
                    <th class="userstory">Story</th>
                    <th class="todo">TODO</th>
                    <th class="doing">Doing</th>
                    <th class="review">Review</th>
                    <th class="done">Done</th>
                </tr>
                {
                    for $user-story in $user-stories
                    let $id := $user-story/*[local-name() = 'id']/text()
                    let $sub-tasks :=
                        for $sub-task in $issues-filtered-by-version[./*[local-name() = 'parent' and @id = $id]]
                        let $id := $sub-task/*[local-name() = 'id']/text()
                        order by $sub-task/*[local-name() = 'priority']/@id descending, $sub-task/*[local-name() = 'id']/text()
                        return
                            ($sub-task,
                            for $sub-task in $issues-filtered-by-version[./*[local-name() = 'parent' and @id = $id]]
                            order by $sub-task/*[local-name() = 'priority']/@id descending, $sub-task/*[local-name() = 'id']/text()
                            return $sub-task
                            )
                        
                    return
                        <tr>
                            <td class="userstory">
                                {local:display-issue($user-story)}
                            </td>
                            <td class="todo">
                                {
                                    for $sub-task in $sub-tasks[./*[local-name() = 'status' and @name = 'New']]
                                    return local:display-issue($sub-task)
                                }
                            </td>
                            <td class="doing">
                                {
                                    for $sub-task in $sub-tasks[./*[local-name() = 'status' and contains(' In Progress Feedback On Hold', @name)]]
                                    return local:display-issue($sub-task)
                                }
                            </td>
                            <td class="review">
                                {
                                    for $sub-task in $sub-tasks[./*[local-name() = 'status' and @name = 'Resolved']]
                                    return local:display-issue($sub-task)
                                }
                            </td>
                            <td class="done">
                                {
                                    for $sub-task in $sub-tasks[./*[local-name() = 'status' and @name = 'Closed']]
                                    return local:display-issue($sub-task)
                                }
                            </td>
                        </tr>
                }
            </table>                
            </div>
        </body>
    </html>
