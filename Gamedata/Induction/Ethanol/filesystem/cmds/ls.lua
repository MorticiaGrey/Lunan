if flags[1] == nil then
    local path = ""
    if args[1] == nil then
        path = lunan.getSelectedPath()
    else
        path = lunan.getSelectedPath() .. args[1]
    end

    local children = lunan.getFolderChildren(path, true)
    if not (children == nil) then
        local buffer = ""
        for i in pairs(children) do
            buffer = children[i] .. htmlTab .. buffer
        end
        if not (buffer == "") then
            print(buffer)
        end
    end
elseif flags[1] == "-l" then
    local path = ""
    if args[1] == nil then
        path = lunan.getSelectedPath()
    else
        path = lunan.getSelectedPath() .. args[1]
    end

    local children = lunan.getFolderChildrenLong(path, true)
    if not (children == nil) then
        for i in pairs(children) do
            print(children[i])
        end
    end
end
