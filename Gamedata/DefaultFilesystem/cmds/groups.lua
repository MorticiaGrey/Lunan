local groups
if args[1] == nil then
    if flags[1] == nil then
        groups = lunan.getGroups(lunan.getCurrUser())
    elseif flags[1] == "-a" then
        groups = lunan.getGroups("-a");
    end
else
    groups = lunan.getGroups(args[1])
end

if not groups then
    print("error: user not found")
    return
end

if not (groups == nil) then
    local buffer = ""
    for i in pairs(groups) do
        buffer = groups[i] .. " " .. buffer
    end
    if not (buffer == "") then
        print(buffer)
    end
end
