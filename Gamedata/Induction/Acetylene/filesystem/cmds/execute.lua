local passedArgs = ""
if args[1] == nil then
    print("usage: execute [FILE]")
    return
elseif tableLength(args) >= 2 then
    for i = 2, tableLength(args), 1 do
        passedArgs = passedArgs .. " " .. args[i]
    end
end

local path = lunan.getSelectedPath() .. args[1]
if not lunan.executeScript(path, passedArgs) then
    print(args[1] .. ": no such file or directory")
end
